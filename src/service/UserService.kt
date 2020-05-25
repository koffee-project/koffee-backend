package eu.yeger.service

import eu.yeger.model.dto.Credentials
import eu.yeger.model.dto.PartialUser
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.Token
import eu.yeger.model.dto.UserListEntry
import eu.yeger.model.dto.UserProfile

interface UserService {

    suspend fun getAllUsers(): Result<List<UserListEntry>>

    suspend fun getUserById(id: String): Result<UserProfile>

    suspend fun createUser(partialUser: PartialUser): Result<String>

    suspend fun updateUser(partialUser: PartialUser): Result<String>

    suspend fun deleteUserById(id: String): Result<String>

    suspend fun login(credentials: Credentials): Result<Token>
}
