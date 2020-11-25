package ru.iopump.koproc

import org.apache.commons.io.input.TeeInputStream
import org.apache.commons.io.output.TeeOutputStream
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


/**
 * The result with active [Process].
 * There is two way using:
 * - call blocking [result] with timeout from [config] and get [KoprocResult]
 * - get [process] and use its Java API
 * - process 'out' and 'errOut' will be multiplied and also provided via [readAvailableOut] and [readAvailableErrOut] during process executing.
 */
@Suppress("MemberVisibilityCanBePrivate")
data class KprocCall(
    val command: String,
    val process: Process,
    val config: KoprocConfig,
    val startDateTime: LocalDateTime = LocalDateTime.now()
) : Closeable {
    private val closed = AtomicBoolean()

    private val outCurrent: ByteArrayOutputStream = ByteArrayOutputStream()
    private val errOutCurrent: ByteArrayOutputStream = ByteArrayOutputStream()
    private val teeOutIn: TeeInputStream

    private val out: ByteArrayOutputStream = ByteArrayOutputStream()
    private val errOut: ByteArrayOutputStream = ByteArrayOutputStream()
    private val teeErrOutIn: TeeInputStream

    private var outOnClose: String? = null
    private var errOutOnClose: String? = null

    private companion object {
        private val log = LoggerFactory.getLogger("koproc")
    }

    init {
        val teeOutOut = TeeOutputStream(outCurrent, out)
        teeOutIn = TeeInputStream(process.inputStream, teeOutOut, true)

        val teeErrOutOut = TeeOutputStream(errOutCurrent, errOut)
        teeErrOutIn = TeeInputStream(process.errorStream, teeErrOutOut, true)
    }

    /**
     * Read available bytes from 'out'.
     * You can read available bytes only ones.
     * It has side effect on source [Process.getInputStream]
     */
    val readAvailableOut: String
        get() {
            read()
            return outCurrent.toString(StandardCharsets.UTF_8)
        }

    /**
     * Read available bytes from 'errOut'.
     * You can read available bytes only ones.
     * It has side effect on source [Process.getErrorStream]
     */
    val readAvailableErrOut: String
        get() {
            read()
            return errOutCurrent.toString(StandardCharsets.UTF_8)
        }

    /**
     * Blocking function (property).
     *
     * Call [Process.waitFor] and return result as [KoprocResult].
     * Save result in lazy property.
     * After execution you should use [KoprocResult] and leave this [KprocCall] for GC.
     * It has side effect on source [Process.getInputStream] and [Process.getErrorStream]
     */
    val result by lazy { waitResult(config.timeoutSec) }

    /**
     * Blocking function.
     *
     * Call [Process.waitFor] and write result to [KoprocResult].
     * After execution you should use [KoprocResult] and leave this [KprocCall] for GC.
     * This function doesn't respect to [Process] state and can be executed again with process in illegal state.
     * To avoid errors prefer [result] lazy property.
     * It has side effect on source [Process.getInputStream] and [Process.getErrorStream]
     *
     * @param timeoutSec Timeout sec
     */
    fun waitResult(timeoutSec: Long = config.timeoutSec) = runCatching { process.apply { waitFor(timeoutSec, TimeUnit.SECONDS) } }
        .mapCatching { prc ->
            read(true)
            KoprocResult(
                this,
                prc.exitValue(),
                outOnClose ?: "",
                errOutOnClose ?: "",
                prc.pid(),
                if (prc is FailedProcess) prc.throwable else null
            )
        }.getOrElse { throwable ->
            read(true)
            KoprocResult(
                this,
                process.exitValue(),
                outOnClose ?: "",
                errOutOnClose ?: "",
                process.pid(),
                throwable
            )
        }.also {
            log.debug("Process finished. Info: $it")
            close()
        }

    /**
     * Call [Process.destroyForcibly] and [Process.destroy].
     * Close all internal [KprocCall] streams and cached 'out' and 'errOut'.
     * It has side effect on source [Process.getInputStream] and [Process.getErrorStream]
     */
    override fun close() {
        runCatching {
            process.destroyForcibly().destroy()

            outOnClose = out.toString(StandardCharsets.UTF_8) // Copy to String cache before close
            out.close()
            errOutOnClose = errOut.toString(StandardCharsets.UTF_8) // Copy to String cache before close
            errOut.close()

            outCurrent.close()
            errOutCurrent.close()

            synchronized(closed) {
                teeOutIn.close()
                teeErrOutIn.close()
                closed.set(true)
            }

        }.onFailure { log.debug("Process closing error. Info: ${toString()}", it) }

        log.debug("Process closed. Info: ${toString()}")
    }

    private fun read(cached: Boolean = false) {
        if (!closed.get()) {
            synchronized(closed) {
                teeOutIn.readNBytes(teeOutIn.available())
                teeErrOutIn.readNBytes(teeErrOutIn.available())
                if (cached) {
                    outOnClose = out.toString(StandardCharsets.UTF_8)
                    errOutOnClose = errOut.toString(StandardCharsets.UTF_8)
                }
            }
        }
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