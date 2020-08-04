package eu.yeger.koffee.service

import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.repository.FakeUserRepository
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.testUser
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

private const val testImageString = "42"

class ProfileImageServiceTests {

    private lateinit var profileImageService: ProfileImageService

    @BeforeTest
    fun setup() {
        val userRepository = FakeUserRepository()
        runBlocking {
            userRepository.insert(testUser)
        }
        profileImageService = DefaultProfileImageService(userRepository = userRepository)
    }

    @Test
    fun `verify that images can be created`() {
        runBlocking {
            // When profile image is created
            profileImageService.saveProfileImageForUser(testUser.id, testImageString).status shouldBe HttpStatusCode.Created

            // Then profile image can be retrieved
            val imageResult = profileImageService.getProfileImageByUserId(testUser.id) as Result.Success
            imageResult.status shouldBe HttpStatusCode.OK
            imageResult.data.encodedImage shouldBe testImageString

            profileImageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `verify that images cannot be created if user does not exist`() {
        runBlocking {
            // When profile image is not created
            profileImageService.saveProfileImageForUser(
                "idDoesNotExist",
                testImageString
            ).status shouldBe HttpStatusCode.NotFound

            // Then profile image cannot be retrieved either
            profileImageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            profileImageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that images can be deleted`() {
        runBlocking {
            // When profile image is deleted
            profileImageService.saveProfileImageForUser(testUser.id, testImageString).status shouldBe HttpStatusCode.Created
            profileImageService.deleteProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.OK

            // Then profile image cannot be retrieved
            profileImageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            profileImageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that images cannot be deleted if they do not exist`() {
        runBlocking {
            // When profile image is deleted
            profileImageService.deleteProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound

            // Then profile image cannot be retrieved
            profileImageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            profileImageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }
}
