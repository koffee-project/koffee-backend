package eu.yeger.service

import eu.yeger.model.Result
import eu.yeger.model.User
import eu.yeger.repository.UserRepository
import io.ktor.http.HttpStatusCode

class DefaultUserService(private val userRepository: UserRepository) : UserService {

    override suspend fun getAllUsers(): Result<List<User>> =
        Result(
            status = HttpStatusCode.OK,
            data = userRepository.getAll()
        )

    override suspend fun getUserByName(name: String): Result<User?> =
        userRepository.getByName(name = name).let { user ->
            Result(
                status = if (user == null) HttpStatusCode.NotFound else HttpStatusCode.OK,
                data = user
            )
        }

    override suspend fun createUser(user: User): Result<String> =
        when (userRepository.hasUserWithName(name = user.name)) {
            true -> Result(
                status = HttpStatusCode.Conflict,
                data = "User with that name already exists"
            )
            false -> {
                userRepository.insert(user)
                Result(
                    status = HttpStatusCode.Created,
                    data = "Created $user"
                )
            }
        }

    override suspend fun updateUser(user: User): Result<String> =
        when (userRepository.hasUserWithName(name = user.name)) {
            true -> {
                userRepository.insert(user)
                Result(
                    status = HttpStatusCode.OK,
                    data = "Updated $user"
                )
            }
            false -> Result(
                status = HttpStatusCode.Conflict,
                data = "User with that name does not exist"
            )
        }

    override suspend fun deleteUserByName(name: String): Result<String> =
        when (userRepository.hasUserWithName(name = name)) {
            true -> {
                userRepository.removeByName(name)
                Result(
                    status = HttpStatusCode.OK,
                    data = "Deleted $name"
                )
            }
            false -> Result(
                status = HttpStatusCode.NotFound,
                data = "User with that name does not exist"
            )
        }
}
