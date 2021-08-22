package com.pipeline.processedfile

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ProcessedFile(
    @Id
    @Column(length = 64)
    val filename: String,
    var timestamp: Instant = Instant.now(),
    var finishedProcessing: Boolean = false,
    var lastLineProcessed: Int = -1,
)