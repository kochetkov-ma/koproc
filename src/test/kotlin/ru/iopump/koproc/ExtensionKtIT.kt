package ru.iopump.koproc

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.types.shouldNotBeInstanceOf
import kotlinx.coroutines.delay
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Paths

@Suppress("BlockingMethodInNonBlockingContext")
class ExtensionKtIT : StringSpec() {


    private companion object {
        private val log = LoggerFactory.getLogger("koproc")
    }

    init {
        "Java process should started by 'startProcess', provide http access and stopped by 'close' method" {
            val jarPath = Paths.get(this::class.java.getResource("/koproc-sample.jar").toURI())
            val jarAccessUrl = URL("http://localhost:8000/test")
            val koproc = "java -jar $jarPath".startProcess { timeoutSec = 5 }

            koproc.use {
                delay(500)

                log.info("[TEST] Call: $it")

                with(jarAccessUrl.openConnection() as HttpURLConnection) {
                    responseCode shouldBe 200
                    inputStream.bufferedReader().readText() shouldBe "OK"
                }
                it.readAvailableOut.shouldNotBeBlank()
                log.info("[TEST] Out: ${it.readAvailableOut}")
            }

            assertThrows<ConnectException> {
                with(jarAccessUrl.openConnection() as HttpURLConnection) {
                    responseCode shouldBe 404
                }
            }

            koproc.result.out.shouldNotBeBlank()
            log.info("[TEST] Result: ${koproc.result}")
        }

        "Java process should started by 'startProcess' and closed after timeout with the 'KoprocResult'" {
            val jarPath = Paths.get(this::class.java.getResource("/koproc-sample.jar").toURI())
            val koproc = "java -jar $jarPath".startProcess { timeoutSec = 1 }

            koproc.use { call ->
                delay(500)
                log.info("[TEST] Result: ${call.result}")
                call.result.asClue {
                    it.code shouldBe 1
                    it.error.shouldNotBeInstanceOf<IllegalThreadStateException>()
                    it.out.shouldNotBeBlank()
                    it.errorOut.shouldBeBlank()
                }
            }
        }

        "Java should display version by 'startCommand' with exit code 0" {
            val koproc = "java -version".startCommand { timeoutSec = 5 }

            log.info("$koproc")

            koproc.asClue {
                it.code shouldBe 0
                it.error.shouldBeNull()
                (it.out + it.errorOut).shouldContain("version") // Java may print to out or err
            }
        }

        "Shell or Cmd should print dirs" {
            val os = System.getProperty("os.name").toLowerCase()
            val cmd = if (os.contains("win")) Paths.get(this::class.java.getResource("/cmd.bat").toURI())
            else "pwd"

            val koproc = "$cmd".startCommand { timeoutSec = 5 }

            koproc.asClue {
                it.code shouldBe 0
                it.error.shouldBeNull()
                it.out.shouldNotBeBlank()
                it.errorOut.shouldBeBlank()
            }
        }

        "Fail start process" {
            assertThrows<RuntimeException> {
                "error".startCommand().throwOnAnyFailure()
            }
        }

        "Closing after success execution and get the result after closing" {
            val koproc = "java -version".startProcess { timeoutSec = 5 }

            koproc.use {
                it.waitResult()
            }

            koproc.result.asClue {
                it.code shouldBe 0
                it.out.shouldBeBlank()
                it.errorOut.shouldNotBeBlank()
                it.error.shouldBeNull()

                it.throwOnUnSuccessCode()
                it.throwOnError()
                it.throwOnAnyFailure()
            }
        }
    }
}
