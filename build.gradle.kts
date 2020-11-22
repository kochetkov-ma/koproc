import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

tasks {

    withType<KotlinCompile>().configureEach {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "11"
    }

    wrapper {
        version = project.property("wrapper.version") as String
        distributionType = Wrapper.DistributionType.ALL
    }

    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(Libs.Logger.slf4j)
    implementation(Libs.Logger.kotlinLog)

    testImplementation(Libs.Kotest.junit5)
    testImplementation(Libs.Kotest.assertionsCore)
}