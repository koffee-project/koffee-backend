package eu.yeger.repository

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User

/**
 * Repository for [User]s.
 *
 * @author Jan Müller
 */
interface UserRepository {

    /**
     * Retrieves all [User]s from the repository.
     *
     * @return All available [User]s.
     */
    suspend fun getAll(): List<User>

    /**
     * Retrieves a [User] from the repository.
     *
     * @param id The id of the [User].
     * @return The [User] if available.
     */
    suspend fun getById(id: String): User?

    /**
     * Checks if the repository contains a specific [User].
     *
     * @param id The id of the [User].
     * @return true if the repository contains the [User].
     */
    suspend fun hasUserWithId(id: String): Boolean

    /**
     * Inserts a [User] into the repository.
     *
     * @param user The [User] to be inserted.
     */
    suspend fun insert(user: User)

    /**
     * Updates a [User] from the repository.
     *
     * @param id The id of the [User] to be updated.
     * @param name The new name of the [User].
     * @param isAdmin The new isAdmin of the [User].
     * @param password The optional new password of the [User].
     */
    suspend fun update(id: String, name: String, isAdmin: Boolean, password: String?)

    /**
     * Removes a [User] from the repository.
     *
     * @param id The id of the [User] to be removed.
     */
    suspend fun removeById(id: String)

    /**
     * Adds a [Transaction] to a [User] from the repository.
     *
     * @param id The id of the [User].
     * @param transaction The [Transaction] to be added.
     */
    suspend fun addTransaction(id: String, transaction: Transaction)
}
