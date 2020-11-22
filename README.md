koproc
---

Kotlin process builder

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