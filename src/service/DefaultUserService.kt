package eu.yeger.service

import eu.yeger.model.Result
import eu.yeger.model.User
import eu.yeger.repository.UserRepository
import io.ktor.http.HttpStatusCode

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<User>> =
        Result(status = HttpStatusCode.OK, data = userRepository.getAll())

    override suspend fun getUserById(id: String): Result<User?> =
        userRepository.getById(id = id).let { user ->
            val status = if (user == null) HttpStatusCode.NotFound else HttpStatusCode.OK
            Result(status = status, data = user)
        }

    override suspend fun createUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> Result(status = HttpStatusCode.Conflict, data = "User with that id already exists")
            false -> validated(user) {
                userRepository.insert(user)
                Result(status = HttpStatusCode.Created, data = "Created $user")
            }
        }

    override suspend fun updateUser(user: User): Result<String> =
        when (userRepository.hasUserWithId(id = user.id)) {
            true -> validated(user) {
                userRepository.insert(user)
                Result(status = HttpStatusCode.OK, data = "Updated $user")
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

    private inline fun validated(user: User, block: () -> Result<String>): Result<String> =
        when (user.isValid()) {
            true -> block()
            false -> Result(status = HttpStatusCode.UnprocessableEntity, data = "$user is invalid")
        }

    private fun User.isValid(): Boolean =
        id.isNotBlank() && name.isNotBlank()
}
