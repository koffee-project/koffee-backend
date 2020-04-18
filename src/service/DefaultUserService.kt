package eu.yeger.service

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.authentication.matches
import eu.yeger.authentication.withHashedPassword
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Credentials
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.UserCreationRequest
import eu.yeger.model.dto.UserListEntry
import eu.yeger.model.dto.UserProfile
import eu.yeger.model.dto.asProfile
import eu.yeger.model.dto.asUser
import eu.yeger.model.dto.asUserListEntry
import eu.yeger.repository.UserRepository

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

    override suspend fun createUser(userCreationRequest: UserCreationRequest): Result<String> {
        val user = userCreationRequest.asUser()
        return when (userRepository.hasUserWithId(id = user.id)) {
            true -> Result.Conflict("User with that id already exists")
            false -> user.processed { hashedUser ->
                userRepository.insert(hashedUser)
                Result.Created("Created ${hashedUser.asProfile()}")
            }
        }
    }

    override suspend fun updateUser(user: User): Result<String> {
        return when (userRepository.hasUserWithId(id = user.id)) {
            true -> user.processed { hashedUser ->
                userRepository.insert(hashedUser)
                Result.OK("Updated ${hashedUser.asProfile()}")
            }
            false -> Result.Conflict("User with that id does not exist")
        }
    }

    override suspend fun deleteUserById(id: String): Result<String> {
        return when (userRepository.hasUserWithId(id = id)) {
            true -> {
                userRepository.removeById(id)
                Result.OK("Deleted $id")
            }
            false -> Result.NotFound("User with that id does not exist")
        }
    }

    override suspend fun login(credentials: Credentials): Result<String> {
        return when (val user = userRepository.getById(id = credentials.id)) {
            null -> Result.Unauthorized("ID or password incorrect")
            else -> credentials.validatedForUser(user) {
                when (val token = JWTConfiguration.makeToken(user)) {
                    null -> Result.Forbidden("${user.id} does not have administrator privileges")
                    else -> Result.OK(token)
                }
            }
        }
    }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> {
        return when (this matches user) {
            true -> block()
            false -> Result.Unauthorized("ID or password incorrect")
        }
    }

    private inline fun User.processed(block: (User) -> Result<String>): Result<String> {
        return when (this.isValid()) {
            true -> block(this.withHashedPassword())
            false -> Result.UnprocessableEntity("Invalid user data")
        }
    }

    private fun User.isValid(): Boolean {
        return id.isNotBlank() &&
            name.isNotBlank() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true &&
            password?.length ?: 8 >= 8
    }
}
