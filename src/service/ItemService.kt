package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.dto.Result

interface ItemService {

    suspend fun getAllItems(): Result<List<Item>>

    suspend fun getItemById(id: String): Result<Item>

    suspend fun createItem(item: Item): Result<String>

    suspend fun updateItem(item: Item): Result<String>

    suspend fun deleteItemById(id: String): Result<String>
}
