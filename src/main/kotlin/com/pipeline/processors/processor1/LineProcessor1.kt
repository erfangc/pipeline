package com.pipeline.processors.processor1

import com.pipeline.content.Content
import com.pipeline.content.ContentRepository
import com.pipeline.processedfile.ProcessedFileRepository
import javax.transaction.Transactional

open class LineProcessor1(
    private val processedFileRepository: ProcessedFileRepository,
    private val contentRepository: ContentRepository,
    private val filename: String,
) {
    @Transactional
    open fun processLine(line: String, currentLine: Int) {
        val processedFile = processedFileRepository.getById(filename)
        if (processedFile.lastLineProcessed >= currentLine) {
            // skip processing
        } else {
            contentRepository.save(Content(filename = filename, lineNumber = currentLine.toString(), content = line))
            processedFileRepository.save(processedFile.apply { lastLineProcessed = currentLine })
        }
    }
}