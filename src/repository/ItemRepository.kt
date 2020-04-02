package eu.yeger.repository

import eu.yeger.model.Item

interface ItemRepository {

    suspend fun getAll(): List<Item>

    suspend fun getItemByName(name: String): Item?

    suspend fun hasItemWithName(name: String): Boolean

    suspend fun insert(item: Item)

    suspend fun removeByName(name: String)
}
