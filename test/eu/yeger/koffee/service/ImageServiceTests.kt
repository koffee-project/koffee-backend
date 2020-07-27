package eu.yeger.koffee.service

import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.repository.FakeImageRepository
import eu.yeger.koffee.repository.FakeUserRepository
import eu.yeger.koffee.utility.shouldBe
import eu.yeger.koffee.utility.testUser
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

private const val testImageString = "42"

class ImageServiceTests {

    private lateinit var imageService: ImageService

    @BeforeTest
    fun setup() {
        val userRepository = FakeUserRepository()
        runBlocking {
            userRepository.insert(testUser)
        }
        imageService = DefaultImageService(imageRepository = FakeImageRepository(), userRepository = userRepository)
    }

    @Test
    fun `verify that images can be created`() {
        runBlocking {
            // When profile image is created
            imageService.saveProfileImageForUser(testUser.id, testImageString).status shouldBe HttpStatusCode.Created

            // Then profile image can be retrieved
            val imageResult = imageService.getProfileImageByUserId(testUser.id) as Result.Success
            imageResult.status shouldBe HttpStatusCode.OK
            imageResult.data.id shouldBe testUser.id
            imageResult.data.encodedImage shouldBe testImageString

            imageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `verify that images cannot be created if user does not exist`() {
        runBlocking {
            // When profile image is not created
            imageService.saveProfileImageForUser(
                "idDoesNotExist",
                testImageString
            ).status shouldBe HttpStatusCode.NotFound

            // Then profile image cannot be retrieved either
            imageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            imageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that images can be deleted`() {
        runBlocking {
            // When profile image is deleted
            imageService.saveProfileImageForUser(testUser.id, testImageString).status shouldBe HttpStatusCode.Created
            imageService.deleteProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.OK

            // Then profile image cannot be retrieved
            imageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            imageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `verify that images cannot be deleted if they do not exist`() {
        runBlocking {
            // When profile image is deleted
            imageService.deleteProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound

            // Then profile image cannot be retrieved
            imageService.getProfileImageByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
            imageService.getProfileImageTimestampByUserId(testUser.id).status shouldBe HttpStatusCode.NotFound
        }
    }
}
