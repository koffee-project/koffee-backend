package eu.yeger.koffee.service

import eu.yeger.koffee.authentication.JWTConfiguration
import eu.yeger.koffee.authentication.matches
import eu.yeger.koffee.authentication.withHashedPassword
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Credentials
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.Token
import eu.yeger.koffee.model.dto.UserListEntry
import eu.yeger.koffee.model.dto.UserProfile
import eu.yeger.koffee.model.dto.andThen
import eu.yeger.koffee.model.dto.asProfile
import eu.yeger.koffee.model.dto.asUser
import eu.yeger.koffee.model.dto.asUserListEntry
import eu.yeger.koffee.model.dto.map
import eu.yeger.koffee.model.dto.mapFailureStatus
import eu.yeger.koffee.model.dto.withResult
import eu.yeger.koffee.repository.ImageRepository
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.utility.ID_OR_PASSWORD_INCORRECT
import eu.yeger.koffee.utility.INVALID_USER_DATA
import eu.yeger.koffee.utility.NO_ADMINISTRATOR_PRIVILEGES
import eu.yeger.koffee.utility.USER_DELETED_SUCCESSFULLY
import eu.yeger.koffee.utility.USER_UPDATED_SUCCESSFULLY
import eu.yeger.koffee.utility.validateUserDoesNotExist
import eu.yeger.koffee.utility.validateUserExists
import io.ktor.http.HttpStatusCode

/**
 * Default [UserService] implementation.
 *
 * @author Jan Müller
 */
class DefaultUserService(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : UserService {

    override suspend fun getAllUsers(): Result<List<UserListEntry>> {
        val entries = userRepository.getAll().map(User::asUserListEntry)
        return Result.ok(entries)
    }

    override suspend fun getUserById(id: String): Result<UserProfile> {
        return userRepository
            .validateUserExists(id)
            .andThen { user -> Result.ok(user.asProfile()) }
    }

    override suspend fun createUser(partialUser: PartialUser): Result<String> {
        return userRepository
            .validateUserDoesNotExist(partialUser)
            .andThen { processPartialUser(it) }
            .withResult { hashedUser -> userRepository.insert(hashedUser) }
            .andThen { Result.created(partialUser.id) }
    }

    override suspend fun updateUser(partialUser: PartialUser): Result<String> {
        return userRepository
            .validateUserExists(partialUser.id)
            .andThen { processPartialUser(partialUser) }
            .withResult { hashedUser ->
                userRepository.update(
                    id = hashedUser.id,
                    name = hashedUser.name,
                    isAdmin = hashedUser.isAdmin,
                    password = hashedUser.password
                )
            }
            .andThen { Result.ok(USER_UPDATED_SUCCESSFULLY) }
    }

    override suspend fun deleteUserById(id: String): Result<String> {
        return userRepository
            .validateUserExists(id)
            .withResult {
                userRepository.removeById(id)
                imageRepository.removeByUserId(id)
            }
            .andThen { Result.ok(USER_DELETED_SUCCESSFULLY) }
    }

    override suspend fun login(credentials: Credentials): Result<Token> {
        return userRepository
            .validateUserExists(credentials.id)
            .mapFailureStatus { HttpStatusCode.Unauthorized }
            .andThen { credentials.validateForUser(it) }
            .andThen { generateTokenForUser(it) }
            .andThen { token -> Result.ok(token) }
    }

    private suspend fun processPartialUser(partialUser: PartialUser): Result<User> {
        return validatePartialUser(partialUser).map { it.asUser().withHashedPassword() }
    }

    private fun validatePartialUser(partialUser: PartialUser): Result<PartialUser> {
        return when (partialUser.isValid()) {
            true -> Result.ok(partialUser)
            false -> Result.unprocessableEntity(INVALID_USER_DATA)
        }
    }

    private fun PartialUser.isValid(): Boolean {
        return id.isNotBlank() &&
            name.isNotBlank() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true &&
            password?.length ?: 8 >= 8
    }

    private fun Credentials.validateForUser(user: User): Result<User> {
        return when (this matches user) {
            true -> Result.ok(user)
            false -> Result.unauthorized(ID_OR_PASSWORD_INCORRECT)
        }
    }

    private fun generateTokenForUser(user: User): Result<Token> {
        return when (val token = JWTConfiguration.makeToken(user)) {
            null -> Result.forbidden(NO_ADMINISTRATOR_PRIVILEGES)
            else -> Result.ok(token)
        }
    }
}
