package com.pipeline

import com.pipeline.processedfile.ProcessedFile
import com.pipeline.processedfile.ProcessedFileRepository
import com.pipeline.processors.processor1.FileProcessor1
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

@DataJpaTest
class FileConsumerTest {

    @Autowired
    private lateinit var processedFileRepository: ProcessedFileRepository
    private lateinit var fileProcessor1: FileProcessor1
    private lateinit var fileConsumer: FileConsumer
    private lateinit var queue: LinkedBlockingQueue<Path>
    private lateinit var executor: ExecutorService

    @BeforeEach
    internal fun setUp() {
        queue = LinkedBlockingQueue<Path>()
        executor = Executors.newSingleThreadExecutor()
        fileProcessor1 = mockk()
        every { fileProcessor1.processFile(any()) } just runs

        fileConsumer = FileConsumer(
            processedFileRepository = processedFileRepository,
            fileProcessor1 = fileProcessor1,
        )
    }

    @Test
    fun `process a file that has not been processed before`() {

        assert(processedFileRepository.findByIdOrNull("document.txt") == null)

        val path = File("/tmp/document.txt").toPath()
        queue.offer(path)

        // we need to start a Thread that stops the consumer
        // so it doesn't keep blocking the current thread
        executor.submit {
            Thread.sleep(1500)
            fileConsumer.stopConsuming()
        }

        fileConsumer.startConsuming(queue)

        // wait at most 2 seconds to find that the document has finished processing
        assertTimeout(Duration.ofSeconds(2)) {
            assert(processedFileRepository.findByIdOrNull("document.txt")?.finishedProcessing == true)
        }

        // verify that the correct underlying process was called
        verify { fileProcessor1.processFile(path) }
    }

    @Test
    fun `process a file that has been processed before`() {
        // pre-mark the file as already processed - by inserting a row into the db
        processedFileRepository.save(ProcessedFile(filename = "document.txt", finishedProcessing = true))

        val path = File("/tmp/document.txt").toPath()
        queue.offer(path)


        // we need to start a Thread that stops the consumer
        // so it doesn't keep blocking the current thread
        executor.submit {
            Thread.sleep(1500)
            fileConsumer.stopConsuming()
        }

        fileConsumer.startConsuming(queue)

        // verify that the correct underlying process was called
        verify(exactly = 0, timeout = 1000) { fileProcessor1.processFile(path) }
    }

}