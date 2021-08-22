package com.pipeline

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync


@EnableAsync
@SpringBootApplication
class PipelineApplication {

    @Bean
    fun cmd(fileDispatcher: FileDispatcher, fileWatcher: FileWatcher) = CommandLineRunner {
        fileWatcher.startWatchingDirectory()
        fileWatcher.processExistingFilesInDirectory()
        fileDispatcher.run()
    }
}

fun main(args: Array<String>) {
    runApplication<PipelineApplication>(*args)
}

