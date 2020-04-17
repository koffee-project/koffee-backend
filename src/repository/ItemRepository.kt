package eu.yeger.repository

import eu.yeger.model.domain.Item

interface ItemRepository {

    suspend fun getAll(): List<Item>

    suspend fun getById(id: String): Item?

    suspend fun hasItemWithId(id: String): Boolean

    suspend fun insert(item: Item)

    suspend fun removeById(id: String)
}
