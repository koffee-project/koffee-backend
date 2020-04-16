package eu.yeger.service

import eu.yeger.authentication.JWTConfiguration
import eu.yeger.authentication.matches
import eu.yeger.authentication.withHashedPassword
import eu.yeger.model.BalanceChange
import eu.yeger.model.Credentials
import eu.yeger.model.Profile
import eu.yeger.model.Result
import eu.yeger.model.User
import eu.yeger.model.profile
import eu.yeger.repository.UserRepository
import eu.yeger.utility.hasTwoDecimalPlaces

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<Profile>> {
        val profiles = userRepository.getAll().map(User::profile)
        return Result.OK(profiles)
    }

    override suspend fun getUserById(id: String): Result<Profile?> =
        userRepository.getById(id = id).let { user ->
            when (val profile = user?.profile) {
                null -> Result.NotFound(null)
                else -> Result.OK(profile)
            }
        }

    override suspend fun createUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> Result.Conflict("User with that id already exists")
            false -> user.validated { hashedUser ->
                userRepository.insert(hashedUser)
                Result.Created("Created ${hashedUser.profile}")
            }
        }

    override suspend fun updateUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> user.validated { hashedUser ->
                userRepository.insert(hashedUser)
                Result.OK("Updated ${hashedUser.profile}")
            }
            false -> Result.Conflict("User with that id does not exist")
        }

    override suspend fun deleteUserById(id: String): Result<String> =
        when (userRepository.hasUserWithId(id = id)) {
            true -> {
                userRepository.removeById(id)
                Result.OK("Deleted $id")
            }
            false -> Result.NotFound("User with that id does not exist")
        }

    override suspend fun login(credentials: Credentials): Result<String> =
        when (val user = userRepository.getById(id = credentials.id)) {
            null -> Result.Unauthorized("ID or password incorrect")
            else -> credentials.validatedForUser(user) {
                when (val token = JWTConfiguration.makeToken(user)) {
                    null -> Result.Forbidden("${user.id} does not have administrator privileges")
                    else -> Result.OK(token)
                }
            }
        }

    override suspend fun updateBalance(id: String, balanceChange: BalanceChange): Result<String> =
        when (userRepository.hasUserWithId(id = id)) {
            false -> Result.Conflict("User with that id does not exist")
            true -> {
                userRepository.updateBalance(id = id, change = balanceChange.change)
                Result.OK("Balance updated successfully")
            }
        }

    private inline fun Credentials.validatedForUser(user: User, block: () -> Result<String>): Result<String> =
        when (this matches user) {
            true -> block()
            false -> Result.Unauthorized("ID or password incorrect")
        }

    private inline fun User.validated(block: (User) -> Result<String>): Result<String> =
        when (this.isValid()) {
            true -> block(this.withHashedPassword())
            false -> Result.UnprocessableEntity("Invalid user data")
        }

    private fun User.isValid(): Boolean =
        id.isNotBlank() &&
            name.isNotBlank() &&
            balance.hasTwoDecimalPlaces() &&
            (!isAdmin || password?.isNotBlank() ?: false) &&
            password?.isNotBlank() ?: true &&
            password?.length ?: 8 >= 8
}
