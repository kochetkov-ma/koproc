package ru.iopump.koproc

import java.io.File

data class KoprocConfig internal constructor(var timeoutSec: Long = Long.MAX_VALUE,
                                             var workingDir: File? = null)