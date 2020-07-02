package eu.yeger.repository

import eu.yeger.model.domain.Item

/**
 * Repository for [Item]s.
 *
 * @author Jan Müller
 */
interface ItemRepository {

    /**
     * Retrieves all [Item]s from the repository.
     *
     * @return All available [Item]s.
     */
    suspend fun getAll(): List<Item>

    /**
     * Retrieves an [Item] from the repository.
     *
     * @param id The id of the [Item].
     * @return The [Item] if available.
     */
    suspend fun getById(id: String): Item?

    /**
     * Checks if the repository contains a specific [Item].
     *
     * @param id The id of the [Item].
     * @return true if the repository contains the [Item].
     */
    suspend fun hasItemWithId(id: String): Boolean

    /**
     * Inserts an [Item] into the repository.
     *
     * @param item The [Item] to be inserted.
     */
    suspend fun insert(item: Item)

    /**
     * Removes an [Item] from the repository.
     *
     * @param id The id of the [Item] to be removed.
     */
    suspend fun removeById(id: String)

    /**
     * Updates the amount of an [Item] from the repository.
     *
     * @param id The id of the [Item] to be updated.
     * @param change The delta to be changed.
     */
    suspend fun updateAmount(id: String, change: Int)
}
