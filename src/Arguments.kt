package eu.yeger

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import eu.yeger.Arguments.databaseHost
import eu.yeger.Arguments.databaseName
import eu.yeger.Arguments.databasePort
import eu.yeger.Arguments.defaultAdminSecret
import eu.yeger.Arguments.hmacSecret
import eu.yeger.Arguments.url

/**
 * Contains the arguments of this application.
 *
 * @property databaseHost The host of the MongoDB.
 * @property databaseName The name of the MongoDB.
 * @property databasePort The port of the MongoDB.
 * @property defaultAdminSecret The name of the admin secret file.
 * @property hmacSecret The name of the hmac secret file.
 * @property url The URL of this server.
 *
 * @author Jan Müller
 */
object Arguments : Arkenv() {

    val databaseHost by argument<String>()

    val databaseName by argument<String> {
        defaultValue = { "koffee-database" }
    }

    val databasePort by argument<String> {
        defaultValue = { "27017" }
    }

    val defaultAdminSecret by argument<String>()

    val hmacSecret by argument<String>()

    val url by argument<String>()
}
