package eu.yeger.authentication

import eu.yeger.model.domain.User
import eu.yeger.model.dto.Credentials
import org.mindrot.jbcrypt.BCrypt

fun User.withHashedPassword() =
    when (password) {
        null -> this
        else -> this.copy(password = BCrypt.hashpw(password, BCrypt.gensalt()))
    }

infix fun Credentials.matches(user: User): Boolean {
    return user.password != null &&
        this.id == user.id &&
        BCrypt.checkpw(this.password, user.password)
}
