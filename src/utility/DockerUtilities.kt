package eu.yeger.utility

import com.cars.framework.secrets.DockerSecrets

fun loadDockerSecrets(fileName: String? = null): Map<String, String> =
    try {
        when (fileName) {
            null -> DockerSecrets.load()
            else -> DockerSecrets.loadFromFile(fileName)
        }
    } catch (e: Exception) {
        mapOf()
    }

fun readDockerSecret(name: String): String? = loadDockerSecrets()[name]
