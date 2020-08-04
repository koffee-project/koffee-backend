package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.PartialUser
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.UserListEntry
import eu.yeger.koffee.model.dto.asDomainUser
import eu.yeger.koffee.model.dto.asProfile
import eu.yeger.koffee.model.dto.asUserListEntry
import eu.yeger.koffee.repository.FakeUserRepository
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.testPartialUser
import eu.yeger.koffee.utility.testUser
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
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(testUser.id) as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
        }
    }

    @Test
    fun `verify that users can be created without passwords`() {
        runBlocking {
            // When user is created
            val userCreationRequest = testPartialUser.copy(password = null)
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.Created

            // Then user can be retrieved
            val result = userService.getUserById(testUser.id) as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe userCreationRequest.asDomainUser().asProfile()
        }
    }

    @Test
    fun `verify that users cannot be created twice`() {
        runBlocking {
            // When user is created
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created

            // Then user can not be created again
            val result = userService.getUserById(testUser.id) as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Conflict
        }
    }

    @Test
    fun `verify that users cannot be created with invalid ids`() {
        runBlocking {
            // When user is created with invalid id
            val userCreationRequest = testPartialUser.copy(id = "    ")
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(userCreationRequest.id) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that users cannot be created with invalid names`() {
        runBlocking {
            // When user is created with invalid name
            val userCreationRequest = testPartialUser.copy(name = "    ")
            userService.createUser(userCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(userCreationRequest.id) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that users cannot be created with invalid passwords`() {
        runBlocking {
            // When users are created with invalid passwords
            val firstUserCreationRequest = testPartialUser.copy(password = "1234567")
            val secondUserCreationRequest = testPartialUser.copy(password = "          ")
            userService.createUser(firstUserCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity
            userService.createUser(secondUserCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then users can not be retrieved
            val result = userService.getAllUsers() as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data.size shouldBe 0
        }
    }

    @Test
    fun `verify that admins cannot be created without passwords`() {
        runBlocking {
            // When admin is created without password
            val adminCreationRequest = testPartialUser.copy(isAdmin = true, password = null)
            userService.createUser(adminCreationRequest).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then user can not be retrieved
            val result = userService.getUserById(adminCreationRequest.id) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that users can be updated`() {
        runBlocking {
            // When user is created and updated
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            val updatedUser = testPartialUser.copy(name = "NewName")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.OK

            // Then retrieved user has new values
            val result = userService.getUserById(testUser.id) as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe updatedUser.asDomainUser().asProfile()
        }
    }

    @Test
    fun `verify that users cannot be updated if they do not exist`() {
        runBlocking {
            // When non-existent user is updated
            userService.updateUser(testPartialUser).status shouldBe HttpStatusCode.NotFound

            // Then user was not created either
            val result = userService.getUserById(testUser.id) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that users cannot be updated with invalid names`() {
        runBlocking {
            // When user is created and updated with invalid name
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            val updatedUser = testPartialUser.copy(name = "   ")
            userService.updateUser(updatedUser).status shouldBe HttpStatusCode.UnprocessableEntity

            // Then retrieved user was not updated
            val result = userService.getUserById(testUser.id) as Result.Success
            result.status shouldBe HttpStatusCode.OK
            result.data shouldBe testUser.asProfile()
        }
    }

    @Test
    fun `verify that users can be deleted`() {
        runBlocking {
            // When user is created and deleted
            userService.createUser(testPartialUser).status shouldBe HttpStatusCode.Created
            userService.deleteUserById(testUser.id).status shouldBe HttpStatusCode.OK

            // Then user can not be retrieved
            val result = userService.getUserById(testUser.id) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that users cannot be deleted if they do not exist`() {
        runBlocking {
            val userId = "userName"
            // When non-existent user is deleted
            userService.deleteUserById(userId).status shouldBe HttpStatusCode.NotFound

            // Then user was not created either
            val result = userService.getUserById(userId) as Result.Failure
            result.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that all users can be retrieved`() {
        runBlocking {
            // When multiple users are created
            val firstRequest = testPartialUser.copy(id = "firstUser")
            val secondRequest = testPartialUser.copy(id = "secondUser")
            userService.createUser(firstRequest).status shouldBe HttpStatusCode.Created
            userService.createUser(secondRequest).status shouldBe HttpStatusCode.Created

            // Then all users are retrieved
            val result = userService.getAllUsers() as Result.Success
            result.status shouldBe HttpStatusCode.OK
            val expected = listOf(firstRequest, secondRequest)
                .map(PartialUser::asDomainUser)
                .map(User::asUserListEntry)
            result.data.sortedBy(UserListEntry::id) shouldBe expected.sortedBy(UserListEntry::id)
        }
    }
}
