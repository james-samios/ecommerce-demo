package software.samios.api.utility

import io.github.cdimascio.dotenv.Dotenv
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This object is responsible for loading Environment Variables.
 */
object EnvLoader {

    private val dotenv: Dotenv? = if (Files.exists(Paths.get("./api/.env"))) {
        Dotenv.configure()
            .directory("./api/.env")
            .load()
    } else {
        null
    }

    fun getEnvVariable(name: String): String? {
        return dotenv?.get(name) ?: System.getenv(name)
    }
}