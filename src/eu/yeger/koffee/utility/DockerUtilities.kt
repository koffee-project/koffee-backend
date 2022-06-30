package eu.yeger.koffee.utility

import com.cars.framework.secrets.DockerSecretLoadException
import com.cars.framework.secrets.DockerSecrets

/**
 * Loads Docker secrets from the file system.
 *
 * @param fileName The name of the secret file.
 * @return A [Map] containing the secret keys and values.
 *
 * @author Jan Müller
 */
fun loadDockerSecrets(fileName: String? = null): Map<String, String> {
    return try {
        when (fileName) {
            null -> DockerSecrets.load()
            else -> DockerSecrets.loadFromFile(fileName)
        }
    } catch (e: DockerSecretLoadException) {
        emptyMap()
    }
}
