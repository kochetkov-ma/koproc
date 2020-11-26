package ru.iopump.koproc

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Koproc configuration passed to [startProcess] and [startCommand].
 *
 * @param timeoutSec Waiting process finish timeout in seconds
 * @param workingDir Working directory [ProcessBuilder.directory]. Default is 'null' means current user directory.
 * @param charset Charset for 'out' and 'err' encoding. Default is [StandardCharsets.UTF_8].
 */
data class KoprocConfig internal constructor(
    var timeoutSec: Long = Long.MAX_VALUE,
    var workingDir: File? = null,
    var charset: Charset = StandardCharsets.UTF_8
)