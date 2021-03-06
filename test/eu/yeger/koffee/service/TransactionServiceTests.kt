package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.Transaction
import eu.yeger.koffee.model.dto.Funding
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.repository.FakeItemRepository
import eu.yeger.koffee.repository.FakeUserRepository
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.testFunding
import eu.yeger.koffee.utility.testItem
import eu.yeger.koffee.utility.testPartialUser
import eu.yeger.koffee.utility.testPurchase
import eu.yeger.koffee.utility.testUser
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TransactionServiceTests {

    private lateinit var userService: UserService

    private lateinit var itemService: ItemService

    private lateinit var transactionService: TransactionService

    @BeforeEach
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe testFunding.amount
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 1
        }
    }

    @Test
    fun `verify that user balances cannot be topped up if the user does not exist`() {
        runBlocking {
            // When non-existent user tops up their balance
            val userId = "doesNotExist"
            transactionService.processFunding(userId, testFunding).status shouldBe HttpStatusCode.NotFound

            // Then user was not created either
            val result = userService.getUserById(userId) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
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

            // Then funding was not processed
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 0
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe -(testItem.price * testPurchase.amount)
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            val transaction = transactionResult.data.first() as Transaction.Purchase
            transaction.run {
                value shouldBe -(testPurchase.amount * testItem.price)
                itemId shouldBe testItem.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(testItem.id) as Result.Success
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data.amount shouldBe testItem.amount!! - testPurchase.amount
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe -(testItem.price * testPurchase.amount)
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            val transaction = transactionResult.data.first() as Transaction.Purchase
            transaction.run {
                value shouldBe -(testPurchase.amount * testItem.price)
                itemId shouldBe testItem.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(item.id) as Result.Success
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data.amount shouldBe item.amount
        }
    }

    @Test
    fun `verify that purchases of non-existent items are not possible`() {
        runBlocking {
            // When user requests an invalid purchase
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            transactionService.processPurchase(testUser.id, testPurchase).status shouldBe HttpStatusCode.NotFound

            // Then the transaction was not processed
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 0
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

            // Then the transaction was not processed
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 0
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            val transaction = transactionResult.data.last() as Transaction.Refund
            transaction.run {
                value shouldBe testPurchase.amount * testItem.price
                itemId shouldBe testItem.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(testItem.id) as Result.Success
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data.amount shouldBe testItem.amount
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            val transaction = transactionResult.data.last() as Transaction.Refund
            transaction.run {
                value shouldBe testPurchase.amount * item.price
                itemId shouldBe item.id
                amount shouldBe testPurchase.amount
            }
            val itemResult = itemService.getItemById(item.id) as Result.Success
            itemResult.status shouldBe HttpStatusCode.OK
            itemResult.data.amount shouldBe item.amount
        }
    }

    @Test
    fun `verify that refunds items are not possible if no purchase was made`() {
        runBlocking {
            // When user requests a refund without any purchase made
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            transactionService.refundLastPurchase(testUser.id).status shouldBe HttpStatusCode.Conflict

            // Then no transaction was created
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 0
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
            val userResult = userService.getUserById(testUser.id) as Result.Success
            userResult.status shouldBe HttpStatusCode.OK
            userResult.data.balance shouldBe 0.0
            val transactionResult = transactionService.getTransactionsOfUser(testUser.id) as Result.Success
            transactionResult.status shouldBe HttpStatusCode.OK
            transactionResult.data.size shouldBe 2
        }
    }
}
