package eu.yeger.koffee.authentication

import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Credentials
import org.mindrot.jbcrypt.BCrypt

/**
 * Hashes the password of a [User] if it is not null.
 *
 * @receiver The [User] to be hashed.
 * @return The [User] with a hashed password.
 *
 * @author Jan Müller
 */
fun User.withHashedPassword() =
    when (password) {
        null -> this
        else -> this.copy(password = BCrypt.hashpw(password, BCrypt.gensalt()))
    }

/**
 * Checks if [Credentials] match a [User].
 *
 * @receiver The [Credentials] to be checked.
 * @param user The [User] to be checked against.
 * @return true if the [Credentials] match the [User].
 *
 * @author Jan Müller
 */
infix fun Credentials.matches(user: User): Boolean {
    return user.password != null &&
        this.id == user.id &&
        BCrypt.checkpw(this.password, user.password)
}
