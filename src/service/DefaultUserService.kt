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
            true -> Result.Conflict("User with that id already exists.")
            false -> partialUser.processed { hashedUser ->
                userRepository.insert(hashedUser)
                Result.Created("User created successfully.")
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
                Result.OK("User updated successfully.")
            }
            false -> Result.Conflict("User with that id does not exist.")
        }
    }

    override suspend fun deleteUserById(id: String): Result<String> {
        return when (userRepository.hasUserWithId(id = id)) {
            true -> {
                userRepository.removeById(id)
                Result.OK("Deleted $id")
            }
            false -> Result.NotFound("User with that id does not exist.")
        }
    }

    override suspend fun login(credentials: Credentials): Result<String> {
        return when (val user = userRepository.getById(id = credentials.id)) {
            null -> Result.Unauthorized("ID or password incorrect.")
            else -> credentials.validatedForUser(user) {
                when (val token = JWTConfiguration.makeToken(user)) {
                    null -> Result.Forbidden("${user.id} does not have administrator privileges.")
                    else -> Result.OK(token)
                }
            }
        }
    }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> {
        return when (this matches user) {
            true -> block()
            false -> Result.Unauthorized("ID or password incorrect.")
        }
    }

    private inline fun PartialUser.processed(block: (User) -> Result<String>): Result<String> {
        return when (this.isValid()) {
            true -> block(this.asUser().withHashedPassword())
            false -> Result.UnprocessableEntity("Invalid user data.")
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
