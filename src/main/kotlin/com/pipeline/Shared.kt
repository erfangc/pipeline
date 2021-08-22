package com.pipeline

import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue

object Shared {
    val dirToMonitor = "/Users/erfangchen/inbound"
    val queue = LinkedBlockingQueue<Path>()
}