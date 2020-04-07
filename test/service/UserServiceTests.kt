package eu.yeger.service

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
            // when user is created
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created

            // then user can be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user
        }
    }

    @Test
    fun `verify that users cannot be created twice`() {
        runBlocking {
            // when user is created
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created

            // then user can not be created again
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user
            userService.createUser(user).status shouldBe HttpStatusCode.Conflict
        }
    }

    @Test
    fun `verify that users cannot be created with invalid ids`() {
        runBlocking {
            // when user is created with invalid id
            val user = testUser.copy(id = "    ")
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid names`() {
        runBlocking {
            // when user is created with invalid name
            val user = testUser.copy(name = "    ")
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be created with invalid balances`() {
        runBlocking {
            // when user is created with invalid balance
            val user = testUser.copy(balance = 100.12345)
            userService.createUser(user).status shouldBe HttpStatusCode.UnprocessableEntity

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users can be updated`() {
        runBlocking {
            // when user is created and updated
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(balance = 50.0)
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.OK

            // then retrieved user has new values
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe updatedUser
        }
    }

    @Test
    fun `verify that users cannot be updated if they do not exist`() {
        runBlocking {
            // when non-existent user is updated
            val user = testUser
            userService.updateUser(user).status shouldBe HttpStatusCode.Conflict

            // then user was not created either
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid names`() {
        runBlocking {
            // when user is created and updated with invalid name
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(name = "   ")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // then retrieved user was not updated
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid balances`() {
        runBlocking {
            // when user is created and updated with invalid balance
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            val updatedUser = user.copy(balance = 1.23456)
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // then retrieved user was not updated
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe user
        }
    }

    @Test
    fun `verify that users can be deleted`() {
        runBlocking {
            // when user is created and deleted
            val user = testUser
            userService.createUser(user).status shouldBe HttpStatusCode.Created
            userService.deleteUserById(user.id).status shouldBe HttpStatusCode.OK

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that users cannot be deleted if they do not exist`() {
        runBlocking {
            val id = "userName"
            // when non-existent user is deleted
            userService.deleteUserById(id).status shouldBe HttpStatusCode.NotFound

            // then user was not created either
            val result = userService.getUserById(id)
            result.status shouldBe HttpStatusCode.NotFound
            result.data shouldBe null
        }
    }

    @Test
    fun `verify that all users can be retrieved`() {
        runBlocking {
            // when multiple users are created
            val firstUser = testUser.copy(id = "firstUser")
            val secondUser = testUser.copy(id = "secondUser")
            userService.createUser(firstUser).status shouldBe HttpStatusCode.Created
            userService.createUser(secondUser).status shouldBe HttpStatusCode.Created

            // then all users are retrieved
            val result = userService.getAllUsers()
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe listOf(firstUser, secondUser)
        }
    }
}
