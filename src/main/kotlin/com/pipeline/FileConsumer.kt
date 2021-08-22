package com.pipeline

import com.pipeline.processedfile.ProcessedFile
import com.pipeline.processedfile.ProcessedFileRepository
import com.pipeline.processors.processor1.FileProcessor1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.io.path.name

@Service
class FileConsumer(
    private val processedFileRepository: ProcessedFileRepository,
    private val fileProcessor1: FileProcessor1,
) {

    private val log: Logger = LoggerFactory.getLogger(FileConsumer::class.java)
    private var running = true

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("Stopping ${this::class.simpleName} to consume file queue")
            stopConsuming()
        })
    }

    @Async
    fun startConsuming(queue: LinkedBlockingQueue<Path>) {

        log.info("Started ${this::class.simpleName} to consume file queue")
        var path: Path?

        while (running) {
            path = queue.poll(1, TimeUnit.SECONDS)
            if (path == null) {
                continue
            }
            val filename = path.name
            val processedFile = processedFileRepository.findByIdOrNull(filename)
            if (processedFile?.finishedProcessing == true) {
                log.info("Skipped $filename because it has already been processed")
                continue
            } else {
                log.info("Processing $filename")
                markFileForProcessingIfNotExists(processedFile, filename)

                // ----------------------------------
                // decide which file processor to use
                fileProcessor1.processFile(path)
                // ----------------------------------

                markFileAsProcessed(filename)
                log.info("Finished processed $filename")
            }
        }
    }

    fun stopConsuming() {
        running = false
    }

    private fun markFileForProcessingIfNotExists(processedFile: ProcessedFile?, filename: String) {
        if (processedFile == null) {
            processedFileRepository.save(ProcessedFile(filename = filename))
        }
    }

    private fun markFileAsProcessed(filename: String) {
        val processedFile = processedFileRepository.getById(filename)
        processedFileRepository.save(processedFile.apply { finishedProcessing = true })
    }

}