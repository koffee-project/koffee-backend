package eu.yeger.service

import eu.yeger.model.User
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
    fun `test saving user`() {
        runBlocking {
            // when user is saved
            val user = User(name = "UserName")
            assertEquals(HttpStatusCode.Created, userService.saveUser(user).statusCode)

            // then user can be retrieved
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.OK, result.statusCode)
            assertEquals(user, result.data)
        }
    }

    @Test
    fun `test saving user twice`() {
        runBlocking {
            // when user is saved
            val user = User(name = "UserName")
            assertEquals(HttpStatusCode.Created, userService.saveUser(user).statusCode)

            // then user can not be saved again
            val result = userService.getUserByName(user.name)
            assertEquals(HttpStatusCode.OK, result.statusCode)
            assertEquals(user, result.data)
            assertEquals(HttpStatusCode.Conflict, userService.saveUser(user).statusCode)
        }
    }

    @Test
    fun `test getting all users`() {
        runBlocking {
            // when multiple users are saved
            val firstUser = User(name = "FirstUser")
            val secondUser = User(name = "SecondUser")
            assertEquals(HttpStatusCode.Created, userService.saveUser(firstUser).statusCode)
            assertEquals(HttpStatusCode.Created, userService.saveUser(secondUser).statusCode)

            // then all users are retrieved
            val result = userService.getAllUsers()
            assertEquals(HttpStatusCode.OK, result.statusCode)
            val expected = listOf(firstUser, secondUser).sortedBy(User::name)
            val actual = result.data.sortedBy(User::name)
            assertEquals(expected, actual)
        }
    }
}
