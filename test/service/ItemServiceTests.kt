package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.repository.TestItemRepository
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class ItemServiceTests {

    private lateinit var itemService: ItemService

    @BeforeTest
    fun setup() {
        itemService = DefaultItemService(itemRepository = TestItemRepository())
    }

    @Test
    fun `test creating item`() {
        runBlocking {
            // when item is created
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)

            // then item can be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `test creating item twice`() {
        runBlocking {
            // when item is created
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)

            // then item can not be created again
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
            assertEquals(HttpStatusCode.Conflict, itemService.createItem(item).status)
        }
    }

    @Test
    fun `test creating item with invalid id`() {
        runBlocking {
            // when item is created without id
            val item = Item(id = "    ", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test creating item with invalid name`() {
        runBlocking {
            // when item is created without name
            val item = Item(id = "water", name = "    ", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test updating item`() {
        runBlocking {
            // when item is created and updated
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            val updatedItem = item.copy(amount = 10, price = 1.5)
            assertEquals(HttpStatusCode.OK, itemService.updateItem(updatedItem).status)

            // then retrieved item has new values
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(updatedItem, result.data)
        }
    }

    @Test
    fun `test updating item that does not exist`() {
        runBlocking {
            // when non-existent item is updated
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Conflict, itemService.updateItem(item).status)

            // then item was not created either
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test updating item with invalid data`() {
        runBlocking {
            // when item is created and updated with invalid data
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            val updatedItem = item.copy(name = "   ")
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.updateItem(updatedItem).status)

            // then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `test deleting item`() {
        runBlocking {
            // when item is created and deleted
            val item = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            assertEquals(HttpStatusCode.OK, itemService.deleteItemById(item.id).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test deleting item that does not exist`() {
        runBlocking {
            val id = "water"
            // when non-existent item is deleted
            assertEquals(HttpStatusCode.NotFound, itemService.deleteItemById(id).status)

            // then item was not created either
            val result = itemService.getItemById(id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test getting all items`() {
        runBlocking {
            // when multiple items are created
            val firstItem = Item(id = "water", name = "Water", amount = 42, price = 0.5)
            val secondItem = Item(id = "coffee", name = "Coffee", amount = 100, price = 1.0)
            assertEquals(HttpStatusCode.Created, itemService.createItem(firstItem).status)
            assertEquals(HttpStatusCode.Created, itemService.createItem(secondItem).status)

            // then all items are retrieved
            val result = itemService.getAllItems()
            assertEquals(HttpStatusCode.OK, result.status)
            val expected = listOf(firstItem, secondItem).sortedBy(Item::id)
            val actual = result.data.sortedBy(Item::id)
            assertEquals(expected, actual)
        }
    }
}
