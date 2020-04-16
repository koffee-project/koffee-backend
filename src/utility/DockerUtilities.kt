package eu.yeger.utility

import com.cars.framework.secrets.DockerSecrets

fun readDockerSecret(name: String): String? =
    try {
        DockerSecrets.load()[name]
    } catch (e: Exception) {
        null
    }
