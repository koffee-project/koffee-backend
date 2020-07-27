package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Credentials
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.Token
import eu.yeger.koffee.model.dto.UserListEntry
import eu.yeger.koffee.model.dto.UserProfile

/**
 * Service for [User]s.
 *
 * @author Jan Müller
 */
interface UserService {

    /**
     * Returns all [User]s.
     *
     * @return The [Result] of the operation.
     */
    suspend fun getAllUsers(): Result<List<UserListEntry>>

    /**
     * Returns the [User] with the given id.
     * Must validate that the [User] exists.
     *
     * @param id The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getUserById(id: String): Result<UserProfile>

    /**
     * Creates a [User].
     * Must validate that the [User] does not exist and the data is valid.
     * Must hash the password.
     *
     * @param partialUser The [PartialUser] used for creating the [User].
     * @return The [Result] of the operation.
     */
    suspend fun createUser(partialUser: PartialUser): Result<String>

    /**
     * Updates a [User].
     * Must validate that the [User] exists and the data is valid.
     * Must hash the password.
     *
     * @param partialUser The [PartialUser] used for updating the [User].
     * @return The [Result] of the operation.
     */
    suspend fun updateUser(partialUser: PartialUser): Result<String>

    /**
     * Deletes a [User] by id.
     * Must validate that the [User] exists.
     *
     * @param id The id of the [User] to be deleted.
     * @return The [Result] of the operation.
     */
    suspend fun deleteUserById(id: String): Result<String>

    /**
     * Performs a login using the given [Credentials].
     * Must validate that the [User] exists and that the credentials are valid.
     *
     * @param credentials The [Credentials] of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun login(credentials: Credentials): Result<Token>
}
