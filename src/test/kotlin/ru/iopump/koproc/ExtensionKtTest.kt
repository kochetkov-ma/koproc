package ru.iopump.koproc

import io.kotest.core.spec.style.StringSpec

class ExtensionKtTest : StringSpec() {

    init {

        "startProcess" {
            val koprocResult = "java -version".startProcess { timeoutSec = 120 }.apply {
                println(process.isAlive)
                println(process.pid())
            }.result

            println(koprocResult)
        }

        "startCommand" {
            val koprocResult = "java -version".startCommand { timeoutSec = 120 }

            println(koprocResult)
        }
    }
}
