koproc
=============================

[![jdk11](https://camo.githubusercontent.com/f3886a668d85acf93f6fec0beadcbb40a5446014/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6a646b2d31312d7265642e737667)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![kotlin](https://img.shields.io/badge/kotlin-1.4.0-green)](https://github.com/JetBrains/kotlin)
[![gradle](https://camo.githubusercontent.com/f7b6b0146f2ee4c36d3da9fa18d709301d91f811/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f746f6f6c2d677261646c652d626c75652e737667)](https://gradle.org/)
![GitHub](https://img.shields.io/github/license/kotest/kotest)
[![maven central](https://img.shields.io/maven-central/v/ru.iopump.koproc/koproc)](http://search.maven.org/#search|ga|1|koproc)


Kotlin process and command executor

## Get Started

Gradle Groovy Dsl:
```groovy
dependencies {
    /** KoProc */
    implementation "ru.iopump:koproc:<version>"
}
```

Gradle Kotlin Dsl:
```kotlin
dependencies {
    /** KoProc */
    implementation("ru.iopump:koproc:<version>")
}
```

### There are two major extension-functions
Start process with long duration, do something with Process Java API via `KoprocCall` and get the result as `KoprocResult` 
```kotlin
import ru.iopump.koproc.startProcess

"cd /".startProcess()

```

Execute short command and wait the result as `KoprocResult`
```kotlin
import ru.iopump.koproc.startProcess

"cd /".startCommand()

```