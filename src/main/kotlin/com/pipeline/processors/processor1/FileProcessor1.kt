package com.pipeline.processors.processor1

import com.pipeline.content.ContentRepository
import com.pipeline.processedfile.ProcessedFileRepository
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.name

@Service
class FileProcessor1(
    private val processedFileRepository: ProcessedFileRepository,
    private val contentRepository: ContentRepository,
) {

    fun processFile(path: Path) {
        val filename = path.name
        val lineProcessor1 = LineProcessor1(
            filename = filename,
            processedFileRepository = processedFileRepository,
            contentRepository = contentRepository
        )
        var currentLine = 0
        path
            .bufferedReader()
            .lines()
            .forEachOrdered { line ->
                lineProcessor1.processLine(line, currentLine)
                currentLine++
            }

    }

}