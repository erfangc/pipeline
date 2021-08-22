package com.pipeline.content

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(ContentId::class)
class Content(
    @Id
    val filename: String,
    @Id
    val lineNumber: String,
    var content: String,
    var timestamp: Instant = Instant.now(),
)