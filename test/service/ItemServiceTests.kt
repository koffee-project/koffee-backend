package eu.yeger.service

import eu.yeger.model.Item
import eu.yeger.repository.FakeItemRepository
import eu.yeger.utility.shouldBe
import eu.yeger.utility.testItem
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
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
            // When item is created
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created

            // Then item can be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
        }
    }

    @Test
    fun `verify that items cannot be created twice`() {
        runBlocking {
            // When item is created
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created

            // Then item can not be created again
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
            itemService.createItem(item).status shouldBe HttpStatusCode.Conflict
        }
    }

    @Test
    fun `verify that items cannot be created with invalid ids`() {
        runBlocking {
            // When item is created with invalid id
            val item = testItem.copy(id = "    ")
            itemService.createItem(item).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be created with invalid names`() {
        runBlocking {
            // When item is created with invalid name
            val item = testItem.copy(name = "    ")
            itemService.createItem(item).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be created with invalid amounts`() {
        runBlocking {
            // When item is created with invalid amount
            val item = testItem.copy(amount = -42)
            itemService.createItem(item).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be created with invalid prices`() {
        runBlocking {
            // When item is created with invalid price
            val item = testItem.copy(price = 0.12345)
            itemService.createItem(item).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be created with negative prices`() {
        runBlocking {
            // When item is created with negative price
            val item = testItem.copy(price = -1.00)
            itemService.createItem(item).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items can be updated`() {
        runBlocking {
            // When item is created and updated
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            val updatedItem = item.copy(amount = 10, price = 1.5)
            itemService.updateItem(updatedItem).status shouldBe HttpStatusCode.OK

            // Then retrieved item has new values
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe updatedItem
        }
    }

    @Test
    fun `verify that items cannot be updated if they do not exist`() {
        runBlocking {
            // When non-existent item is updated
            val item = testItem
            itemService.updateItem(item).status shouldBe HttpStatusCode.Conflict

            // Then item was not created either
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be updated with invalid names`() {
        runBlocking {
            // When item is created and updated with invalid name
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            val updatedItem = item.copy(name = "   ")
            itemService.updateItem(updatedItem).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
        }
    }

    @Test
    fun `verify that items cannot be updated with invalid amounts`() {
        runBlocking {
            // When item is created and updated with invalid amount
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            val updatedItem = item.copy(amount = -42)
            itemService.updateItem(updatedItem).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
        }
    }

    @Test
    fun `verify that items cannot be updated with invalid prices`() {
        runBlocking {
            // When item is created and updated with invalid price
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            val updatedItem = item.copy(price = 0.12345)
            itemService.updateItem(updatedItem).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
        }
    }

    @Test
    fun `verify that items cannot be updated with negative prices`() {
        runBlocking {
            // When item is created and updated with negative price
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            val updatedItem = item.copy(price = -1.00)
            itemService.updateItem(updatedItem).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved item was not updated
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe item
        }
    }

    @Test
    fun `verify that items can be deleted`() {
        runBlocking {
            // When item is created and deleted
            val item = testItem
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            itemService.deleteItemById(item.id).status shouldBe HttpStatusCode.OK

            // Then item can not be retrieved
            val result = itemService.getItemById(item.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that items cannot be deleted if they do not exist`() {
        runBlocking {
            val id = "water"
            // When non-existent item is deleted
            itemService.deleteItemById(id).status shouldBe HttpStatusCode.NotFound

            // Then item was not created either
            val result = itemService.getItemById(id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that all items can be retrieved`() {
        runBlocking {
            // When multiple items are created
            val firstItem = testItem
            val secondItem = Item(id = "coffee", name = "Coffee", amount = 100, price = 1.0)
            itemService.createItem(firstItem).status shouldBe HttpStatusCode.Created
            itemService.createItem(secondItem).status shouldBe HttpStatusCode.Created

            // Then all items are retrieved
            val result = itemService.getAllItems()
            result.status shouldBe HttpStatusCode.OK
            result.data.sortedBy(Item::id) shouldBe listOf(firstItem, secondItem).sortedBy(Item::id)
        }
    }
}
