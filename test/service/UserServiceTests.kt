package eu.yeger.service

import eu.yeger.model.User
import eu.yeger.repository.TestUserRepository
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class UserServiceTests {

    private lateinit var userService: UserService

    @BeforeTest
    fun setup() {
        userService = DefaultUserService(userRepository = TestUserRepository())
    }

    @Test
    fun `test creating user`() {
        runBlocking {
            // when user is created
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)

            // then user can be retrieved
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(user, result.data)
        }
    }

    @Test
    fun `test creating user twice`() {
        runBlocking {
            // when user is created
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)

            // then user can not be created again
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(user, result.data)
            assertEquals(HttpStatusCode.Conflict, userService.createUser(user).status)
        }
    }

    @Test
    fun `test creating user with invalid id`() {
        runBlocking {
            // when user is created without id
            val user = User(id = "    ", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.UnprocessableEntity, userService.createUser(user).status)

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test creating user with invalid name`() {
        runBlocking {
            // when user is created without name
            val user = User(id = "userName", name = "    ", balance = 100.0)
            assertEquals(HttpStatusCode.UnprocessableEntity, userService.createUser(user).status)

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test updating user`() {
        runBlocking {
            // when user is created and updated
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)
            val updatedItem = user.copy(balance = 50.0)
            assertEquals(HttpStatusCode.OK, userService.updateUser(updatedItem).status)

            // then retrieved user has new values
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(updatedItem, result.data)
        }
    }

    @Test
    fun `test updating user that does not exist`() {
        runBlocking {
            // when non-existent user is updated
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Conflict, userService.updateUser(user).status)

            // then user was not created either
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test updating user with invalid data`() {
        runBlocking {
            // when item is created and updated with invalid data
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)
            val updatedUser = user.copy(name = "   ")
            assertEquals(HttpStatusCode.UnprocessableEntity, userService.updateUser(updatedUser).status)

            // then retrieved item was not updated
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(user, result.data)
        }
    }

    @Test
    fun `test deleting user`() {
        runBlocking {
            // when user is created and deleted
            val user = User(id = "userName", name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)
            assertEquals(HttpStatusCode.OK, userService.deleteUserById(user.id).status)

            // then user can not be retrieved
            val result = userService.getUserById(user.id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test deleting user that does not exist`() {
        runBlocking {
            val id = "userName"
            // when non-existent user is deleted
            assertEquals(HttpStatusCode.NotFound, userService.deleteUserById(id).status)

            // then user was not created either
            val result = userService.getUserById(id)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test getting all users`() {
        runBlocking {
            // when multiple users are created
            val firstUser = User(id = "firstUser", name = "FirstUser", balance = 100.0)
            val secondUser = User(id = "secondUser", name = "SecondUser", balance = 200.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(firstUser).status)
            assertEquals(HttpStatusCode.Created, userService.createUser(secondUser).status)

            // then all users are retrieved
            val result = userService.getAllUsers()
            assertEquals(HttpStatusCode.OK, result.status)
            val expected = listOf(firstUser, secondUser).sortedBy(User::id)
            val actual = result.data.sortedBy(User::id)
            assertEquals(expected, actual)
        }
    }
}
