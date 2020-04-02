package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.model.Result

interface ItemService {

    suspend fun getAllItems(): Result<List<Item>>

    suspend fun getItemByName(name: String): Result<Item?>

    suspend fun createItem(item: Item): Result<String>

    suspend fun updateItem(item: Item): Result<String>

    suspend fun deleteItemByName(name: String): Result<String>
}
