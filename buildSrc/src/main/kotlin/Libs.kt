object Libs {

    object Logger {
        const val slf4j = "org.slf4j:slf4j-api:1.7.30"
        const val logback = "ch.qos.logback:logback-classic:1.2.3"
        const val kotlinLog = "io.github.microutils:kotlin-logging-jvm:2.0.3"
        val logbackExcludes = listOf("edu.washington.cs.types.checker", "com.sun.mail")
        val kotlinLogExcludes = listOf("org.jetbrains.kotlin", " org.slf4j")
    }

    object Kotest {
        private const val version = "4.3.1"
        const val junit5 = "io.kotest:kotest-runner-junit5:$version"
        const val assertionsCore = "io.kotest:kotest-assertions-core:$version"
        const val property = "io.kotest:kotest-property:$version"
    }

}