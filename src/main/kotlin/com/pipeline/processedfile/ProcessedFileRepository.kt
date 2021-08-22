package com.pipeline.processedfile

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProcessedFileRepository : JpaRepository<ProcessedFile, String>