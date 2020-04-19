package eu.yeger.service

import eu.yeger.model.domain.Transaction
import eu.yeger.model.dto.Funding
import eu.yeger.repository.FakeItemRepository
import eu.yeger.repository.FakeUserRepository
import eu.yeger.utility.shouldBe
import eu.yeger.utility.testFunding
import eu.yeger.utility.testItem
import eu.yeger.utility.testPartialUser
import eu.yeger.utility.testPurchase
import eu.yeger.utility.testUser
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class TransactionServiceTests {

    private lateinit var userService: UserService

    private lateinit var itemService: ItemService

    private lateinit var transactionService: TransactionService

    @BeforeTest
    fun setup() {
        val userRepository = FakeUserRepository()
        val itemRepository = FakeItemRepository()
        userService = DefaultUserService(userRepository)
        itemService = DefaultItemService(itemRepository)
        transactionService = DefaultTransactionService(userRepository, itemRepository)
    }

    @Test
    fun `verify that user balances can be topped up`() {
        runBlocking {
            // When balance of user is topped up
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            transactionService.processFunding(testUser.id, testFunding).status shouldBe HttpStatusCode.OK

            // Then a transaction can be retrieved
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data?.transactions?.firstOrNull()?.value shouldBe testFunding.amount
        }
    }

    @Test
    fun `verify that user balances cannot be topped up if the user does not exist`() {
        runBlocking {
            // When non-existent user tops up their balance
            val userId = "doesNotExist"
            transactionService.processFunding(userId, testFunding).status shouldBe HttpStatusCode.Conflict

            // Then user was not created either
            val result = userService.getUserById(userId)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that user balances cannot be topped up if the amount is invalid`() {
        runBlocking {
            // When user tops up their balance by an invalid amount
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            val invalidFunding = Funding(amount = 1.2345)
            transactionService.processFunding(
                testUser.id,
                invalidFunding
            ).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user was not created either
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data?.transactions shouldBe emptyList<Transaction>()
        }
    }

    @Test
    fun `verify that purchases are possible`() {
        runBlocking {
            // When user requests a valid purchase
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            itemService.createItem(testItem).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.OK

            // Then the transaction was processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            val transaction = userResult.data?.transactions?.first() as Transaction.Purchase
            transaction.run {
                value shouldBe -(testPurchase.amount * testItem.price)
                itemId shouldBe testItem.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(testItem.id)
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data?.amount shouldBe testItem.amount!! - testPurchase.amount
        }
    }

    @Test
    fun `verify that purchases of unlimited items are possible`() {
        runBlocking {
            // When user requests a valid purchase
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            val item = testItem.copy(amount = null)
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.OK

            // Then the transaction was processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            val transaction = userResult.data?.transactions?.first() as Transaction.Purchase
            transaction.run {
                value shouldBe -(testPurchase.amount * item.price)
                itemId shouldBe item.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(item.id)
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data?.amount shouldBe item.amount
        }
    }

    @Test
    fun `verify that purchases of non-existent items are not possible`() {
        runBlocking {
            // When user requests an invalid purchase
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.Conflict

            // Then the transaction was processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data?.transactions?.size shouldBe 0
        }
    }

    @Test
    fun `verify that purchases with invalid amounts are not possible`() {
        runBlocking {
            // When user requests an invalid purchase
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            itemService.createItem(testItem).status shouldBe HttpStatusCode.Created
            val zeroPurchase = testPurchase.copy(amount = 0)
            val negativePurchase = testPurchase.copy(amount = -42)
            transactionService.processPurchase(
                testUser.id,
                zeroPurchase
            ).status shouldBe HttpStatusCode.UnprocessableEntity
            transactionService.processPurchase(
                testUser.id,
                negativePurchase
            ).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then the transaction was processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data?.transactions?.size shouldBe 0
        }
    }

    @Test
    fun `verify that refunds are possible`() {
        runBlocking {
            // When user requests a refund
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            itemService.createItem(testItem).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.OK
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.OK

            // Then the transaction and refund were processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            val transaction = userResult.data?.transactions?.last() as Transaction.Refund
            transaction.run {
                value shouldBe testPurchase.amount * testItem.price
                itemId shouldBe testItem.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(testItem.id)
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data?.amount shouldBe testItem.amount
        }
    }

    @Test
    fun `verify that refunds of unlimited items are possible`() {
        runBlocking {
            // When user requests a refund
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            val item = testItem.copy(amount = null)
            itemService.createItem(item).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.OK
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.OK

            // Then the transaction and refund were processed
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            val transaction = userResult.data?.transactions?.last() as Transaction.Refund
            transaction.run {
                value shouldBe testPurchase.amount * item.price
                itemId shouldBe item.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(item.id)
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data?.amount shouldBe item.amount
        }
    }

    @Test
    fun `verify that refunds items are not possible if no purchase was made`() {
        runBlocking {
            // When user requests a refund without any purchase made
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.Conflict

            // Then no transaction was created
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data?.transactions?.size shouldBe 0
        }
    }

    @Test
    fun `verify that refunds items are not possible if last purchase has already been refunded`() {
        runBlocking {
            // When user requests a refund but the latest purchase has already been refunded
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            itemService.createItem(testItem).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.OK
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.OK
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.Conflict

            // Then no additional transaction was created
            val userResult = userService.getUserById(testUser.id)
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data?.transactions?.size shouldBe 2
        }
    }
}
