package eu.yeger

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument

object Arguments : Arkenv() {

    val databaseHost: String by argument()

    val databaseName: String by argument {
        defaultValue = { "koffee-database" }
    }

    val databasePort: String by argument {
        defaultValue = { "27017" }
    }

    val defaultAdminId: String by argument {
        defaultValue = { "admin" }
    }

    val defaultAdminName: String by argument {
        defaultValue = { "Admin" }
    }

    val defaultAdminPassword: String by argument {
        defaultValue = { "admin" }
    }

    val url: String by argument()

    val hmacSecret: String by argument()
}
