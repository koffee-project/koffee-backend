package eu.yeger.service

import eu.yeger.model.domain.User
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.UserCreationRequest
import eu.yeger.model.dto.UserListEntry
import eu.yeger.model.dto.asProfile
import eu.yeger.model.dto.asUser
import eu.yeger.model.dto.asUserListEntry
import eu.yeger.repository.FakeUserRepository
import eu.yeger.utility.shouldBe
import eu.yeger.utility.testUser
import eu.yeger.utility.testUserCreationRequest
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
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
        }
    }

    @Test
    fun `verify that users can be created without passwords`() {
        runBlocking {
            // When user is created
            val userCreationRequest = testUserCreationRequest.copy(password = null)
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe userCreationRequest.asUser().asProfile()
        }
    }

    @Test
    fun `verify that users cannot be created twice`() {
        runBlocking {
            // When user is created
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created

            // Then user can not be created again
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Conflict
        }
    }

    @Test
    fun `verify that users cannot be created with invalid ids`() {
        runBlocking {
            // When user is created with invalid id
            val userCreationRequest = testUserCreationRequest.copy(id = "    ")
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(userCreationRequest.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid names`() {
        runBlocking {
            // When user is created with invalid name
            val userCreationRequest = testUserCreationRequest.copy(name = "    ")
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(userCreationRequest.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid passwords`() {
        runBlocking {
            // When users are created with invalid passwords
            val firstUserCreationRequest = testUserCreationRequest.copy(password = "1234567")
            val secondUserCreationRequest = testUserCreationRequest.copy(password = "          ")
            userService.createUser(firstUserCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity
            userService.createUser(secondUserCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then users can not be retrieved
            val result = userService.getAllUsers()
            result.status shouldBe HttpStatusCode.OK
            result.data.size shouldBe 0
        }
    }

    @Test
    fun `verify that admins cannot be created without passwords`() {
        runBlocking {
            // When admin is created without password
            val adminCreationRequest = testUserCreationRequest.copy(isAdmin = true, password = null)
            userService.createUser(adminCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(adminCreationRequest.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users can be updated`() {
        runBlocking {
            // When user is created and updated
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created
            val updatedUser = testUser.copy(name = "NewName")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.OK

            // Then retrieved user has new values
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe updatedUser.asProfile()
        }
    }

    @Test
    fun `verify that users cannot be updated if they do not exist`() {
        runBlocking {
            // When non-existent user is updated
            userService.updateUser(testUser).status shouldBe HttpStatusCode.Conflict

            // Then user was not created either
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid names`() {
        runBlocking {
            // When user is created and updated with invalid name
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created
            val updatedUser = testUser.copy(name = "   ")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved user was not updated
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
        }
    }

    @Test
    fun `verify that users can be deleted`() {
        runBlocking {
            // When user is created and deleted
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created
            userService.deleteUserById(testUser.id).status shouldBe HttpStatusCode.OK

            // Then user can not be retrieved
            val result = userService.getUserById(testUser.id)
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
            val firstRequest = testUserCreationRequest.copy(id = "firstUser")
            val secondRequest = testUserCreationRequest.copy(id = "secondUser")
            userService.createUser(firstRequest).status shouldBe HttpStatusCode.Created
            userService.createUser(secondRequest).status shouldBe HttpStatusCode.Created

            // Then all users are retrieved
            val result = userService.getAllUsers()
            result.status shouldBe HttpStatusCode.OK
            val expected = listOf(firstRequest, secondRequest)
                .map(UserCreationRequest::asUser)
                .map(User::asUserListEntry)
            result.data.sortedBy(UserListEntry::id) shouldBe expected.sortedBy(UserListEntry::id)
        }
    }

    @Test
    fun `verify that user balances can be topped up`() {
        runBlocking {
            // When balance of user is topped up
            userService.createUser(testUserCreationRequest).status shouldBe HttpStatusCode.Created
            val funding = Funding(42.0)
            userService.updateBalance(testUser.id, funding).status shouldBe HttpStatusCode.OK

            // Then a transaction can be retrieved
            val result = userService.getUserById(testUser.id)
            result.status shouldBe HttpStatusCode.OK
            result.data?.transactions?.firstOrNull()?.value shouldBe funding.amount
        }
    }

    @Test
    fun `verify that user balances cannot be topped up if the user does not exist`() {
        runBlocking {
            // When non-existent user tops up their balance
            val userId = "doesNotExist"
            val funding = Funding(42.0)
            userService.updateBalance(userId, funding).status shouldBe HttpStatusCode.Conflict

            // Then user was not created either
            val result = userService.getUserById(userId)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }
}
