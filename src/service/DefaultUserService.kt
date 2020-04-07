package eu.yeger.service

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.model.Credentials
import eu.yeger.model.Result
import eu.yeger.model.User
import eu.yeger.repository.UserRepository
import eu.yeger.utility.hasTwoDecimalPlaces
import io.ktor.http.HttpStatusCode

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<User>> =
        Result(status = HttpStatusCode.OK, data = userRepository.getAll())

    override suspend fun getUserById(id: String): Result<User?> =
        userRepository.getById(id = id).let { user ->
            val status = if (user == null) HttpStatusCode.NotFound else HttpStatusCode.OK
            Result(status = status, data = user)
        }

    // TODO hash passwords
    override suspend fun createUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> Result(status = HttpStatusCode.Conflict, data = "User with that id already exists")
            false -> user.validated {
                userRepository.insert(user)
                val response = user.copy(password = "hidden")
                Result(status = HttpStatusCode.Created, data = "Created $response")
            }
        }

    override suspend fun updateUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> user.validated {
                userRepository.insert(user)
                val response = user.copy(password = "hidden")
                Result(status = HttpStatusCode.OK, data = "Updated $response")
            }
            false -> Result(status = HttpStatusCode.Conflict, data = "User with that id does not exist")
        }

    override suspend fun deleteUserById(id: String): Result<String> =
        when (userRepository.hasUserWithId(id = id)) {
            true -> {
                userRepository.removeById(id)
                Result(status = HttpStatusCode.OK, data = "Deleted $id")
            }
            false -> Result(status = HttpStatusCode.NotFound, data = "User with that id does not exist")
        }

    override suspend fun login(credentials: Credentials): Result<String> =
        when (val user = userRepository.getById(id = credentials.id)) {
            null -> Result(status = HttpStatusCode.Unauthorized, data = "ID or password incorrect")
            else -> credentials.validatedForUser(user) {
                when (val token = JWTConfiguration.makeToken(user)) {
                    null -> Result(status = HttpStatusCode.Forbidden, data = "${user.id} does not have administrator privileges")
                    else -> Result(status = HttpStatusCode.OK, data = token)
                }
            }
        }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> =
        when (this.isValidForUser(user)) {
            true -> block()
            false -> Result(status = HttpStatusCode.Unauthorized, data = "ID or password incorrect")
        }

    // TODO hash passwords
    private fun Credentials.isValidForUser(user: User): Boolean {
        return user.isAdmin &&
            this.id == user.id &&
            this.password == user.password
    }

    private inline fun User.validated(block: () -> Result<String>): Result<String> =
        when (this.isValid()) {
            true -> block()
            false -> Result(status = HttpStatusCode.UnprocessableEntity, data = "$this is invalid")
        }

    // TODO validate passwords
    private fun User.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            balance.hasTwoDecimalPlaces() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true
}
