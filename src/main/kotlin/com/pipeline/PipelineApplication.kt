package com.pipeline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@EnableAsync
@SpringBootApplication
class PipelineApplication

fun main(args: Array<String>) {
    runApplication<PipelineApplication>(*args)
}
