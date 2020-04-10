package eu.yeger.service

import eu.yeger.model.Credentials
import eu.yeger.model.Profile
import eu.yeger.model.Result
import eu.yeger.model.User

interface UserService {

    suspend fun getAllUsers(): Result<List<Profile>>

    suspend fun getUserById(id: String): Result<Profile?>

    suspend fun createUser(user: User): Result<String>

    suspend fun updateUser(user: User): Result<String>

    suspend fun deleteUserById(id: String): Result<String>

    suspend fun login(credentials: Credentials): Result<String>
}
