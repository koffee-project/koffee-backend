package eu.yeger

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument

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
