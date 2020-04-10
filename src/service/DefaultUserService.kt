package eu.yeger.service

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.authentication.matches
import eu.yeger.authentication.withHashedPassword
import eu.yeger.model.Credentials
import eu.yeger.model.Profile
import eu.yeger.model.Result
import eu.yeger.model.User
import eu.yeger.model.profile
import eu.yeger.repository.UserRepository
import eu.yeger.utility.hasTwoDecimalPlaces
import io.ktor.http.HttpStatusCode

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<Profile>> {
        val profiles = userRepository.getAll().map(User::profile)
        return Result(status = HttpStatusCode.OK, data = profiles)
    }

    override suspend fun getUserById(id: String): Result<Profile?> =
        userRepository.getById(id = id).let { user ->
            val status = if (user == null) HttpStatusCode.NotFound else HttpStatusCode.OK
            Result(status = status, data = user?.profile)
        }

    override suspend fun createUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> Result(status = HttpStatusCode.Conflict, data = "User with that id already exists")
            false -> user.validated { hashedUser ->
                userRepository.insert(hashedUser)
                Result(status = HttpStatusCode.Created, data = "Created ${hashedUser.profile}")
            }
        }

    override suspend fun updateUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> user.validated { hashedUser ->
                userRepository.insert(hashedUser)
                Result(status = HttpStatusCode.OK, data = "Updated ${hashedUser.profile}")
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
                    null -> Result(
                        status = HttpStatusCode.Forbidden,
                        data = "${user.id} does not have administrator privileges"
                    )
                    else -> Result(status = HttpStatusCode.OK, data = token)
                }
            }
        }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> =
        when (this matches user) {
            true -> block()
            false -> Result(status = HttpStatusCode.Unauthorized, data = "ID or password incorrect")
        }

    private inline fun User.validated(block: (User) -> Result<String>): Result<String> =
        when (this.isValid()) {
            true -> block(this.withHashedPassword())
            false -> Result(status = HttpStatusCode.UnprocessableEntity, data = "Invalid user data")
        }

    // TODO validate passwords
    private fun User.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            balance.hasTwoDecimalPlaces() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true
}
