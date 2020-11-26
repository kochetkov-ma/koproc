package ru.iopump.koproc

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("koproc")

/**
 * Mutable default timeout for [startProcess]
 *
 * Default: 60 sec.
 */
var koprocDefaultStartProcessTimeoutSec = 60L

/**
 * Mutable default timeout for [startCommand]
 *
 * Default: 10 sec.
 */
var koprocDefaultStartCommandTimeoutSec = 10L

/**
 * Start [this] command via [ProcessBuilder] and provide [KoprocCall] which in turn returns [KoprocResult].
 *
 * @return [KoprocCall]
 */
fun String.startProcess(config: KoprocConfig.() -> Unit = { KoprocConfig(koprocDefaultStartProcessTimeoutSec) }): KoprocCall {
    val cfg = KoprocConfig().apply(config)

    val processBuilder = ProcessBuilder(*this.split.toTypedArray())
        .directory(cfg.workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)

    return runCatching { processBuilder.start() }
        .map { startedProcess -> KoprocCall(this, startedProcess, cfg) }
        .getOrElse { throwable -> KoprocCall(this, FailedProcess(throwable), cfg) }
        .also { log.debug("Process started. Info: $it") }
}

/**
 * Start [this] command via [ProcessBuilder], wait finish and provide [KoprocResult].
 *
 * @return [KoprocResult]
 */
fun String.startCommand(config: KoprocConfig.() -> Unit = { KoprocConfig(koprocDefaultStartCommandTimeoutSec) }): KoprocResult =
    this.startProcess(config).result

private val String.split
    get() = this.split("\\s".toRegex())