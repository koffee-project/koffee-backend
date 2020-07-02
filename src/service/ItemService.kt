package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.dto.Result

/**
 * Service for [Item]s.
 *
 * @author Jan Müller
 */
interface ItemService {

    /**
     * Returns all [Item]s.
     *
     * @return The [Result] of the operation.
     */
    suspend fun getAllItems(): Result<List<Item>>

    /**
     * Returns the [Item] with the given id.
     * Must validate that the [Item] exists.
     *
     * @param id The id of the [Item].
     * @return The [Result] of the operation.
     */
    suspend fun getItemById(id: String): Result<Item>

    /**
     * Creates an [Item].
     * Must validate that the [Item] does not exist and the data is valid.
     *
     * @param item The [Item] to be created.
     * @return The [Result] of the operation.
     */
    suspend fun createItem(item: Item): Result<String>

    /**
     * Updates an [Item].
     * Must validate that the [Item] exists and the data is valid.
     *
     * @param item The [Item] to be updated.
     * @return The [Result] of the operation.
     */
    suspend fun updateItem(item: Item): Result<String>

    /**
     * Deletes an [Item] by id.
     * Must validate that the [Item] exists.
     *
     * @param id The id of the [Item] to be deleted.
     * @return The [Result] of the operation.
     */
    suspend fun deleteItemById(id: String): Result<String>
}
