package ru.iopump.koproc

import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

/**
 * The result with active [Process].
 * There is two way using:
 * - call blocking [result] with timeout from [config] and get [KoprocResult]
 * - get [process] and use its Java API
 */
@Suppress("MemberVisibilityCanBePrivate")
data class KprocCall(
    val command: String,
    val process: Process,
    val config: KoprocConfig,
    val startDateTime: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Blocking function (property).
     *
     * Call [Process.waitFor] and return result as [KoprocResult].
     * Save result in lazy property.
     * After execution you should use [KoprocResult] and leave this [KprocCall] for GC.
     */
    val result by lazy { waitResult(config.timeoutSec) }

    /**
     * Blocking function.
     *
     * Call [Process.waitFor] and write result to [KoprocResult].
     * After execution you should use [KoprocResult] and leave this [KprocCall] for GC.
     * This function doesn't respect to [Process] state and can be executed again with process in illegal state.
     * To avoid errors prefer [result] lazy property.
     *
     * @param timeoutSec Timeout sec
     */
    fun waitResult(timeoutSec: Long = config.timeoutSec) = runCatching { process.apply { waitFor(timeoutSec, TimeUnit.SECONDS) } }
        .mapCatching { prc ->
            KoprocResult(
                this,
                prc.exitValue(),
                prc.inputStream?.bufferedReader()?.readText() ?: "",
                prc.errorStream?.bufferedReader()?.readText() ?: "",
                prc.pid(),
                if (prc is FailedProcess) prc.throwable else null
            )
        }.getOrElse { throwable ->
            KoprocResult(
                this,
                process.exitValue(),
                process.inputStream?.bufferedReader()?.readText() ?: "",
                process.errorStream?.bufferedReader()?.readText() ?: "",
                process.pid(),
                throwable
            )
        }.also {
            log.debug { "[KOPROC] Finished. $it" }
        }
}


internal open class FinishedProcess(private val code: Int) : Process() {

    override fun getOutputStream() = null

    override fun getInputStream() = null

    override fun getErrorStream() = null

    override fun waitFor() = exitValue()

    override fun exitValue() = code

    override fun destroy() {}

    override fun pid() = -1L
}

internal class FailedProcess(val throwable: Throwable) : FinishedProcess(1)