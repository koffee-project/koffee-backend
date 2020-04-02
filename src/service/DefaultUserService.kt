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
        when (val user = userRepository.getByName(name = name)) {
            null -> Result(
                status = HttpStatusCode.NotFound,
                data = null
            )
            else -> Result(
                status = HttpStatusCode.OK,
                data = user
            )
        }

    override suspend fun saveUser(user: User): Result<String> =
        when (getUserByName(name = user.name).data) {
            null -> {
                userRepository.insert(user)
                Result(
                    status = HttpStatusCode.Created,
                    data = "Created $user"
                )
            }
            else -> Result(
                status = HttpStatusCode.Conflict,
                data = "User with that name already exists"
            )
        }
}
