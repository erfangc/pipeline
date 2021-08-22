package com.pipeline

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue


@EnableAsync
@SpringBootApplication
class PipelineApplication {

    @Bean
    fun cmd(fileConsumer: FileConsumer, fileProducer: FileProducer) = CommandLineRunner {
        val queue = LinkedBlockingQueue<Path>()
        fileProducer.startWatchingDirectory(queue)
        fileProducer.processExistingFilesInDirectory(queue)
        fileConsumer.startConsuming(queue)
    }
}

fun main(args: Array<String>) {
    runApplication<PipelineApplication>(*args)
}

