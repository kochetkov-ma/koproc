package ru.iopump.koproc

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("koproc")


/**
 * Start [this] command via [ProcessBuilder] and provide [KprocCall] which in turn returns [KoprocResult].
 *
 * @return [KprocCall]
 */
fun String.startProcess(config: KoprocConfig.() -> Unit = { KoprocConfig(3600) }): KprocCall {
    val cfg = KoprocConfig().apply(config)

    val processBuilder = ProcessBuilder(*this.split.toTypedArray())
        .directory(cfg.workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)

    return runCatching { processBuilder.start() }
        .map { startedProcess -> KprocCall(this, startedProcess, cfg) }
        .getOrElse { throwable -> KprocCall(this, FailedProcess(throwable), cfg) }
        .also { log.debug("Process started. Info: $it") }
}

/**
 * Start [this] command via [ProcessBuilder], wait finish and provide [KoprocResult].
 *
 * @return [KoprocResult]
 */
fun String.startCommand(config: KoprocConfig.() -> Unit = { KoprocConfig(10) }): KoprocResult = this.startProcess(config).result

private val String.split
    get() = this.split("\\s".toRegex())