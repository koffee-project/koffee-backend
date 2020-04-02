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
            val user = User(name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)

            // then user can be retrieved
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(user, result.data)
        }
    }

    @Test
    fun `test creating user twice`() {
        runBlocking {
            // when user is created
            val user = User(name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)

            // then user can not be created again
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(user, result.data)
            assertEquals(HttpStatusCode.Conflict, userService.createUser(user).status)
        }
    }

    @Test
    fun `test updating user`() {
        runBlocking {
            // when user is created and updated
            val user = User(name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(user).status)
            val updatedItem = user.copy(balance = 50.0)
            assertEquals(HttpStatusCode.OK, userService.updateUser(updatedItem).status)

            // then retrieved user has new values
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(updatedItem, result.data)
        }
    }

    @Test
    fun `test updating user that does not exist`() {
        runBlocking {
            // when non-existent user is updated
            val user = User(name = "UserName", balance = 100.0)
            assertEquals(HttpStatusCode.Conflict, userService.updateUser(user).status)

            // then user was not created either
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.NotFound, result.status)
            assertEquals(null, result.data)
        }
    }

    @Test
    fun `test getting all users`() {
        runBlocking {
            // when multiple users are created
            val firstUser = User(name = "FirstUser", balance = 100.0)
            val secondUser = User(name = "SecondUser", balance = 200.0)
            assertEquals(HttpStatusCode.Created, userService.createUser(firstUser).status)
            assertEquals(HttpStatusCode.Created, userService.createUser(secondUser).status)

            // then all users are retrieved
            val result = userService.getAllUsers()
            assertEquals(HttpStatusCode.OK, result.status)
            val expected = listOf(firstUser, secondUser).sortedBy(User::name)
            val actual = result.data.sortedBy(User::name)
            assertEquals(expected, actual)
        }
    }
}
