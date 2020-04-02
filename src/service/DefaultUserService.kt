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

    override suspend fun saveUser(user: User): Result<String> =
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
}
