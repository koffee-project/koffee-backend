package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.Transaction
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.asProfile
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.shouldContain
import eu.yeger.koffee.utility.testItem
import eu.yeger.koffee.utility.testUser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class MongoUserRepositoryTests {

    private lateinit var database: CoroutineDatabase

    private lateinit var mongoUserRepository: MongoUserRepository

    @BeforeEach
    fun setup() {
        database = KMongo.createClient().coroutine.getDatabase("mongo-user-repository-tests-database")
        mongoUserRepository = MongoUserRepository(database)
    }

    @AfterEach
    fun teardown() {
        runBlocking {
            database.drop()
        }
    }

    @Test
    fun `verify that users can be inserted`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
        }
    }

    @Test
    fun `verify that users can be inserted and retrieved by id`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.getById(testUser.id)?.asProfile() shouldBe testUser.asProfile()
        }
    }

    @Test
    fun `verify that multiple users can be inserted and retrieved`() {
        runBlocking {
            val secondUser = testUser.copy(id = "secondUser")
            mongoUserRepository.insert(testUser)
            mongoUserRepository.insert(secondUser)
            val retrievedUsers = mongoUserRepository.getAll().map(User::asProfile)
            retrievedUsers.size shouldBe 2
            retrievedUsers shouldContain testUser.asProfile()
            retrievedUsers shouldContain secondUser.asProfile()
        }
    }

    @Test
    fun `verify that users can be removed`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
            mongoUserRepository.removeById(testUser.id)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe false
        }
    }

    @Test
    fun `verify that users can be updated`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
            val updatedUser = testUser.copy(name = "NewName", isAdmin = true, password = "password")
            mongoUserRepository.update(
                id = updatedUser.id,
                name = updatedUser.name,
                isAdmin = updatedUser.isAdmin,
                password = updatedUser.password
            )
            mongoUserRepository.getById(testUser.id)?.asProfile() shouldBe updatedUser.asProfile()
        }
    }

    @Test
    fun `verify that transactions can be added to users`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
            val funding = Transaction.Funding(value = 42.0)
            mongoUserRepository.addTransaction(testUser.id, funding)
            val purchase =
                Transaction.Purchase(value = -42.0, amount = 1, itemId = testItem.id, itemName = testItem.name)
            mongoUserRepository.addTransaction(testUser.id, purchase)
            val refund = Transaction.Refund(value = 42.0, amount = 1, itemId = testItem.id, itemName = testItem.name)
            mongoUserRepository.addTransaction(testUser.id, refund)
            val retrievedTransactions = mongoUserRepository.getById(testUser.id)!!.transactions
            retrievedTransactions.size shouldBe 3
            retrievedTransactions shouldContain funding
            retrievedTransactions shouldContain purchase
            retrievedTransactions shouldContain refund
        }
    }

    @Test
    fun `verify that profile images can be added to users`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
            val profileImage = ProfileImage("imagestring", timestamp = System.currentTimeMillis())
            mongoUserRepository.addProfileImage(testUser.id, profileImage)
            mongoUserRepository.getById(testUser.id)?.profileImage shouldBe profileImage
        }
    }

    @Test
    fun `verify that profile images can be removed from users`() {
        runBlocking {
            mongoUserRepository.insert(testUser)
            mongoUserRepository.hasUserWithId(testUser.id) shouldBe true
            val profileImage = ProfileImage("imagestring", timestamp = System.currentTimeMillis())
            mongoUserRepository.addProfileImage(testUser.id, profileImage)
            mongoUserRepository.getById(testUser.id)?.profileImage shouldBe profileImage
            mongoUserRepository.removeProfileImage(testUser.id)
            mongoUserRepository.getById(testUser.id)?.profileImage shouldBe null
        }
    }
}
