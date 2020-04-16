package eu.yeger.service

import eu.yeger.model.BalanceChange
import eu.yeger.model.Profile
import eu.yeger.model.User
import eu.yeger.model.profile
import eu.yeger.repository.FakeUserRepository
import eu.yeger.utility.shouldBe
import eu.yeger.utility.testUser
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class UserServiceTests {

    private lateinit var userService: UserService

    @BeforeTest
    fun setup() {
        userService = DefaultUserService(userRepository = FakeUserRepository())
    }

    @Test
    fun `verify that users can be created`() {
        runBlocking {
            // When user is created
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user.profile
        }
    }

    @Test
    fun `verify that users can be created without passwords`() {
        runBlocking {
            // When user is created
            val user = testUser.copy(password = null)
            userService.createUser(user).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user.profile
        }
    }

    @Test
    fun `verify that users cannot be created twice`() {
        runBlocking {
            // When user is created
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created

            // Then user can not be created again
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user.profile
            userService.createUser(user).status shouldBe HttpStatusCode.Conflict
        }
    }

    @Test
    fun `verify that users cannot be created with invalid ids`() {
        runBlocking {
            // When user is created with invalid id
            val user = testUser.copy(id = "    ")
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid names`() {
        runBlocking {
            // When user is created with invalid name
            val user = testUser.copy(name = "    ")
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid balances`() {
        runBlocking {
            // When user is created with invalid balance
            val user = testUser.copy(balance = 100.12345)
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid passwords`() {
        runBlocking {
            // When users are created with invalid passwords
            val firstUser = testUser.copy(password = "1234567")
            val secondUser = testUser.copy(password = "          ")
            userService.createUser(firstUser).status shouldBe HttpStatusCode.UnprocessableEntity
            userService.createUser(secondUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then users can not be retrieved
            val result = userService.getAllUsers()
            result.status shouldBe HttpStatusCode.OK
            result.data.size shouldBe 0
        }
    }

    @Test
    fun `verify that admins cannot be created without passwords`() {
        runBlocking {
            // When user is created with invalid balance
            val admin = testUser.copy(isAdmin = true, password = null)
            userService.createUser(admin).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(admin.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users can be updated`() {
        runBlocking {
            // When user is created and updated
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(balance = 50.0)
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.OK

            // Then retrieved user has new values
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe updatedUser.profile
        }
    }

    @Test
    fun `verify that users cannot be updated if they do not exist`() {
        runBlocking {
            // When non-existent user is updated
            val user = testUser
            userService.updateUser(user).status shouldBe HttpStatusCode.Conflict

            // Then user was not created either
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid names`() {
        runBlocking {
            // When user is created and updated with invalid name
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(name = "   ")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved user was not updated
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user.profile
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid balances`() {
        runBlocking {
            // When user is created and updated with invalid balance
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(balance = 1.23456)
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved user was not updated
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user.profile
        }
    }

    @Test
    fun `verify that users can be deleted`() {
        runBlocking {
            // When user is created and deleted
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            userService.deleteUserById(user.id).status shouldBe HttpStatusCode.OK

            // Then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be deleted if they do not exist`() {
        runBlocking {
            val userId = "userName"
            // When non-existent user is deleted
            userService.deleteUserById(userId).status shouldBe HttpStatusCode.NotFound

            // Then user was not created either
            val result = userService.getUserById(userId)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that all users can be retrieved`() {
        runBlocking {
            // When multiple users are created
            val firstUser = testUser.copy(id = "firstUser")
            val secondUser = testUser.copy(id = "secondUser")
            userService.createUser(firstUser).status shouldBe HttpStatusCode.Created
            userService.createUser(secondUser).status shouldBe HttpStatusCode.Created

            // Then all users are retrieved
            val result = userService.getAllUsers()
            result.status shouldBe HttpStatusCode.OK
            result.data.sortedBy(Profile::id) shouldBe listOf(firstUser, secondUser).map(User::profile).sortedBy(Profile::id)
        }
    }

    @Test
    fun `verify that user balances can be updated`() {
        runBlocking {
            // When balance of user is updated
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val balanceChange = BalanceChange(42.0)
            userService.updateBalance(user.id, balanceChange).status shouldBe HttpStatusCode.OK

            // Then the updated balance is retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data?.balance shouldBe user.balance + balanceChange.change
        }
    }

    @Test
    fun `verify that user balances cannot be updated if the user does not exist`() {
        runBlocking {
            // When balance of non-existent user is updated
            val userId = "doesNotExist"
            val balanceChange = BalanceChange(42.0)
            userService.updateBalance(userId, balanceChange).status shouldBe HttpStatusCode.Conflict

            // Then user was not created either
            val result = userService.getUserById(userId)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }
}
