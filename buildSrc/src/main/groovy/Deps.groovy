class Deps {
    def static logger = new Logger()
    static class Logger {
        def static slf4j = "org.slf4j:slf4j-api:1.7.30"
        def static logback = "ch.qos.logback:logback-classic:1.2.3"
        def static excludes = ["org.jetbrains.kotlin", "org.slf4j", "edu.washington.cs.types.checker", "com.sun.mail"]
    }

    def static kotest = new Kotest()
    static class Kotest {
        def static version = "4.3.1"
        def static junit5 = "io.kotest:kotest-runner-junit5:$version"
        def static assertionsCore = "io.kotest:kotest-assertions-core:$version"
        def static property = "io.kotest:kotest-property:$version"
        def static excludes = ["org.jetbrains.kotlin", "io.mockk"]
    }

    def static kotlin = new Kotlin()
    static class Kotlin {
        def static reflect = "org.jetbrains.kotlin:kotlin-reflect"
    }

    def static common = new Common()
    static class Common {
        def static apacheIo = "commons-io:commons-io:2.8.0"
    }
}
