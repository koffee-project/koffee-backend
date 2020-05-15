package eu.yeger.service

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.authentication.matches
import eu.yeger.authentication.withHashedPassword
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Credentials
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.UserListEntry
import eu.yeger.model.dto.UserProfile
import eu.yeger.model.dto.asProfile
import eu.yeger.model.dto.asUser
import eu.yeger.model.dto.asUserListEntry
import eu.yeger.repository.UserRepository
import eu.yeger.utility.ID_OR_PASSWORD_INCORRECT
import eu.yeger.utility.INVALID_USER_DATA
import eu.yeger.utility.NO_ADMINISTRATOR_PRIVILEGES
import eu.yeger.utility.NO_USER_WITH_THAT_ID
import eu.yeger.utility.USER_CREATED_SUCCESSFULLY
import eu.yeger.utility.USER_DELETED_SUCCESSFULLY
import eu.yeger.utility.USER_UPDATED_SUCCESSFULLY
import eu.yeger.utility.USER_WITH_THAT_ID_ALREADY_EXISTS

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<UserListEntry>> {
        val entries = userRepository.getAll().map(User::asUserListEntry)
        return Result.OK(entries)
    }

    override suspend fun getUserById(id: String): Result<UserProfile?> {
        return userRepository.getById(id = id).let { user ->
            when (val profile = user?.asProfile()) {
                null -> Result.NotFound(null)
                else -> Result.OK(profile)
            }
        }
    }

    override suspend fun createUser(partialUser: PartialUser): Result<String> {
        return when (userRepository.hasUserWithId(id = partialUser.id)) {
            true -> Result.Conflict(USER_WITH_THAT_ID_ALREADY_EXISTS)
            false -> partialUser.processed { hashedUser ->
                userRepository.insert(hashedUser)
                Result.Created(USER_CREATED_SUCCESSFULLY)
            }
        }
    }

    override suspend fun updateUser(partialUser: PartialUser): Result<String> {
        return when (userRepository.hasUserWithId(id = partialUser.id)) {
            true -> partialUser.processed { hashedUser ->
                userRepository.update(
                    id = hashedUser.id,
                    name = hashedUser.name,
                    isAdmin = hashedUser.isAdmin,
                    password = hashedUser.password
                )
                Result.OK(USER_UPDATED_SUCCESSFULLY)
            }
            false -> Result.Conflict(NO_USER_WITH_THAT_ID)
        }
    }

    override suspend fun deleteUserById(id: String): Result<String> {
        return when (userRepository.hasUserWithId(id = id)) {
            true -> {
                userRepository.removeById(id)
                Result.OK(USER_DELETED_SUCCESSFULLY)
            }
            false -> Result.NotFound(NO_USER_WITH_THAT_ID)
        }
    }

    override suspend fun login(credentials: Credentials): Result<String> {
        return when (val user = userRepository.getById(id = credentials.id)) {
            null -> Result.Unauthorized(ID_OR_PASSWORD_INCORRECT)
            else -> credentials.validatedForUser(user) {
                when (val token = JWTConfiguration.makeToken(user)) {
                    null -> Result.Forbidden(NO_ADMINISTRATOR_PRIVILEGES)
                    else -> Result.OK(token)
                }
            }
        }
    }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> {
        return when (this matches user) {
            true -> block()
            false -> Result.Unauthorized(ID_OR_PASSWORD_INCORRECT)
        }
    }

    private inline fun PartialUser.processed(block: (User) -> Result<String>): Result<String> {
        return when (this.isValid()) {
            true -> block(this.asUser().withHashedPassword())
            false -> Result.UnprocessableEntity(INVALID_USER_DATA)
        }
    }

    private fun PartialUser.isValid(): Boolean {
        return id.isNotBlank() &&
            name.isNotBlank() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true &&
            password?.length ?: 8 >= 8
    }
}
