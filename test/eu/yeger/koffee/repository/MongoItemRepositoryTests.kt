package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.shouldContain
import eu.yeger.koffee.utility.testItem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class MongoItemRepositoryTests {

    private lateinit var database: CoroutineDatabase

    private lateinit var mongoItemRepository: MongoItemRepository

    @BeforeTest
    fun setup() {
        database = KMongo.createClient().coroutine.getDatabase("mongo-item-repository-tests-database")
        mongoItemRepository = MongoItemRepository(database)
    }

    @AfterTest
    fun teardown() {
        runBlocking {
            database.drop()
        }
    }

    @Test
    fun `verify that items can be inserted`() {
        runBlocking {
            mongoItemRepository.insert(testItem)
            mongoItemRepository.hasItemWithId(testItem.id) shouldBe true
        }
    }

    @Test
    fun `verify that items can be inserted and retrieved by id`() {
        runBlocking {
            mongoItemRepository.insert(testItem)
            mongoItemRepository.getById(testItem.id) shouldBe testItem
        }
    }

    @Test
    fun `verify that multiple items can be inserted and retrieved`() {
        runBlocking {
            val secondItem = Item(id = "coffee", name = "Coffee", price = 2.0, amount = null)
            mongoItemRepository.insert(testItem)
            mongoItemRepository.insert(secondItem)
            val retrievedItems = mongoItemRepository.getAll()
            retrievedItems shouldContain testItem
            retrievedItems shouldContain secondItem
            retrievedItems.size shouldBe 2
        }
    }

    @Test
    fun `verify that items can be removed`() {
        runBlocking {
            mongoItemRepository.insert(testItem)
            mongoItemRepository.hasItemWithId(testItem.id) shouldBe true
            mongoItemRepository.removeById(testItem.id)
            mongoItemRepository.hasItemWithId(testItem.id) shouldBe false
        }
    }

    @Test
    fun `verify that item amounts can be updated`() {
        runBlocking {
            mongoItemRepository.insert(testItem)
            mongoItemRepository.getById(testItem.id)?.amount shouldBe testItem.amount
            mongoItemRepository.updateAmount(testItem.id, change = -2)
            mongoItemRepository.getById(testItem.id)?.amount shouldBe (testItem.amount!! - 2)
            mongoItemRepository.updateAmount(testItem.id, change = 4)
            mongoItemRepository.getById(testItem.id)?.amount shouldBe (testItem.amount!! - 2 + 4)
        }
    }

    @Test
    fun `verify that item amounts cannot be updated if they are null`() {
        runBlocking {
            val item = testItem.copy(amount = null)
            mongoItemRepository.insert(item)
            mongoItemRepository.getById(item.id)?.amount shouldBe item.amount
            mongoItemRepository.updateAmount(item.id, change = -2)
            mongoItemRepository.getById(item.id)?.amount shouldBe item.amount
            mongoItemRepository.updateAmount(item.id, change = 4)
            mongoItemRepository.getById(item.id)?.amount shouldBe item.amount
        }
    }
}
