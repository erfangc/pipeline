package com.pipeline

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue

@Service
class ApplicationRunner(
    private val fileConsumer: FileConsumer,
    private val fileProducer: FileProducer
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val queue = LinkedBlockingQueue<Path>()
        // start watching before listing
        fileProducer.startWatchingDirectory(queue)
        fileProducer.processExistingFilesInDirectory(queue)
        fileConsumer.startConsuming(queue)
    }
}