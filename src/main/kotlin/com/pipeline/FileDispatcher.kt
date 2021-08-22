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
import kotlin.io.path.name

@Service
class FileDispatcher(
    private val processedFileRepository: ProcessedFileRepository,
    private val fileProcessor1: FileProcessor1,
) {
    val log: Logger = LoggerFactory.getLogger(FileDispatcher::class.java)

    @Async
    fun run() {

        log.info("Started consumer thread")
        var path: Path
        var running = true

        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("Consumer stopping")
            running = false
        })

        while (running) {
            path = Shared.queue.take()
            val filename = path.name
            val processedFile = processedFileRepository.findByIdOrNull(filename)
            if (processedFile?.finishedProcessing == true) {
                log.info("Skipped $filename because it has already been processed")
                continue
            } else {
                log.info("Processing $filename")
                if (processedFile == null) {
                    processedFileRepository.save(ProcessedFile(filename = filename))
                }
                fileProcessor1.processFile(path)
                log.info("Processed $filename")
            }
        }
    }

}