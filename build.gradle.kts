val arkenvVersion: String by project
val dockerSecretsJavaVersion: String by project
val jbcryptVersion: String by project
val kmongoVersion: String by project
val koinVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.github.johnrengelman.shadow")
    id("com.sourcemuse.mongo")
    jacoco
}

group = "eu.yeger"
version = "1.0.0"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("test")
        resources.srcDirs("testresources")
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven(url = "https://kotlin.bintray.com/ktor")
}

dependencies {
    // Kotlin dependencies
    implementation(kotlin("stdlib-jdk8", version = kotlinVersion))
    implementation(kotlin("reflect", version = kotlinVersion))

    // Ktor dependencies
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // Other dependencies
    implementation("com.apurebase:arkenv:$arkenvVersion")
    implementation("com.cars:docker-secrets:$dockerSecretsJavaVersion")
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongoVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Test dependencies
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        // Format the code before compilation
        dependsOn(ktlintFormat)
    }

    ktlintFormat {
        // Delete the folders that are incorrectly generated by ktlintFormat
        doLast {
            delete("/src/main", "/src/test")
        }
    }

    test {
        environment(
            "KOFFEE_SECRET" to "",
            "URL" to "yeger.eu"
        )
        dependsOn(startManagedMongoDb)
    }

    shadowJar {
        archiveFileName.set("${project.name}.jar")
    }

    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = false
        }
    }
}

mongo {
    auth = false
    logging = "none"
}
