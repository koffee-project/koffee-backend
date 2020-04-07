package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.repository.FakeItemRepository
import eu.yeger.utility.testItem
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class ItemServiceTests {

    private lateinit var itemService: ItemService

    @BeforeTest
    fun setup() {
        itemService = DefaultItemService(itemRepository = FakeItemRepository())
    }

    @Test
    fun `verify that items can be created`() {
        runBlocking {
            // when item is created
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)

            // then item can be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `verify that items cannot be created twice`() {
        runBlocking {
            // when item is created
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)

            // then item can not be created again
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
            assertEquals(HttpStatusCode.Conflict, itemService.createItem(item).status)
        }
    }

    @Test
    fun `verify that items cannot be created with invalid ids`() {
        runBlocking {
            // when item is created with invalid id
            val item = testItem.copy(id = "    ")
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be created with invalid names`() {
        runBlocking {
            // when item is created with invalid name
            val item = testItem.copy(name = "    ")
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be created with invalid amounts`() {
        runBlocking {
            // when item is created with invalid amount
            val item = testItem.copy(amount = -42)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be created with invalid prices`() {
        runBlocking {
            // when item is created with invalid price
            val item = testItem.copy(price = 0.12345)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be created with negative prices`() {
        runBlocking {
            // when item is created with negative price
            val item = testItem.copy(price = -1.00)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.createItem(item).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items can be updated`() {
        runBlocking {
            // when item is created and updated
            val item = testItem
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
    fun `verify that items cannot be updated if they do not exist`() {
        runBlocking {
            // when non-existent item is updated
            val item = testItem
            assertEquals(HttpStatusCode.Conflict, itemService.updateItem(item).status)

            // then item was not created either
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be updated with invalid names`() {
        runBlocking {
            // when item is created and updated with invalid name
            val item = testItem
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
    fun `verify that items cannot be updated with invalid amounts`() {
        runBlocking {
            // when item is created and updated with invalid amount
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            val updatedItem = item.copy(amount = -42)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.updateItem(updatedItem).status)

            // then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `verify that items cannot be updated with invalid prices`() {
        runBlocking {
            // when item is created and updated with invalid price
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            val updatedItem = item.copy(price = 0.12345)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.updateItem(updatedItem).status)

            // then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `verify that items cannot be updated with negative prices`() {
        runBlocking {
            // when item is created and updated with negative price
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            val updatedItem = item.copy(price = -1.00)
            assertEquals(HttpStatusCode.UnprocessableEntity, itemService.updateItem(updatedItem).status)

            // then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(item, result.data)
        }
    }

    @Test
    fun `verify that items can be deleted`() {
        runBlocking {
            // when item is created and deleted
            val item = testItem
            assertEquals(HttpStatusCode.Created, itemService.createItem(item).status)
            assertEquals(HttpStatusCode.OK, itemService.deleteItemById(item.id).status)

            // then item can not be retrieved
            val result = itemService.getItemById(item.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `verify that items cannot be deleted if they do not exist`() {
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
    fun `verify that all items can be retrieved`() {
        runBlocking {
            // when multiple items are created
            val firstItem = testItem
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
