package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.andThen
import eu.yeger.koffee.model.dto.map
import eu.yeger.koffee.model.dto.withResult
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.utility.IMAGE_DELETED_SUCCESSFULLY
import eu.yeger.koffee.utility.IMAGE_TOO_LARGE
import eu.yeger.koffee.utility.IMAGE_UPLOADED_SUCCESSFULLY
import eu.yeger.koffee.utility.NO_IMAGE_FOR_THAT_USER_ID
import eu.yeger.koffee.utility.validateUserExists

private const val MAX_IMAGE_STRING_LENGTH = 1_000_000

/**
 * Default [ProfileImageService] implementation.
 *
 * @author Jan Müller
 */
class DefaultProfileImageService(
    private val userRepository: UserRepository
) : ProfileImageService {

    override suspend fun getProfileImageByUserId(userId: String): Result<ProfileImage> {
        return userRepository
            .validateUserExists(userId)
            .andThen { user -> user.validateProfileImageExists() }
            .andThen { Result.ok(it) }
    }

    override suspend fun getProfileImageTimestampByUserId(userId: String): Result<Long> {
        return getProfileImageByUserId(userId)
            .map { profileImage -> profileImage.timestamp }
    }

    override suspend fun saveProfileImageForUser(userId: String, image: String): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { validateProfileImage(ProfileImage(image, System.currentTimeMillis())) }
            .withResult { profileImage -> userRepository.addProfileImage(userId, profileImage) }
            .andThen { Result.created(IMAGE_UPLOADED_SUCCESSFULLY) }
    }

    override suspend fun deleteProfileImageByUserId(userId: String): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { user -> user.validateProfileImageExists() }
            .withResult { userRepository.removeProfileImage(userId) }
            .andThen { Result.ok(IMAGE_DELETED_SUCCESSFULLY) }
    }

    private fun validateProfileImage(profileImage: ProfileImage): Result<ProfileImage> {
        return when {
            profileImage.encodedImage.length > MAX_IMAGE_STRING_LENGTH -> Result.unprocessableEntity(IMAGE_TOO_LARGE)
            else -> Result.ok(profileImage)
        }
    }

    private fun User.validateProfileImageExists(): Result<ProfileImage> {
        return when (profileImage) {
            null -> Result.notFound(NO_IMAGE_FOR_THAT_USER_ID)
            else -> Result.ok(profileImage)
        }
    }
}
