package com.pipeline

import com.pipeline.Shared.dirToMonitor
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.*
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

@Service
class FileWatcher {

    private val log = LoggerFactory.getLogger(FileWatcher::class.java)
    private val watchService = FileSystems.getDefault().newWatchService()
    private val dir: Path = Paths.get(dirToMonitor)

    fun processExistingFilesInDirectory() {
        val existingFiles = dir.listDirectoryEntries().filter { !it.isDirectory() }
        existingFiles.forEach { Shared.queue.offer(it) }
    }

    @Async
    fun startWatchingDirectory() {
        dir.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
        )
        log.info("Started monitoring $dir")
        var key: WatchKey
        while (watchService.take().also { key = it } != null) {
            for (event: WatchEvent<*> in key.pollEvents()) {
                val context = event.context()
                if (context is Path) {
                    log.info("Event kind:${event.kind()} File affected: " + context + "."                    )
                    Shared.queue.offer(File(dirToMonitor, context.name).toPath())
                }
            }
            key.reset()
        }
    }

}