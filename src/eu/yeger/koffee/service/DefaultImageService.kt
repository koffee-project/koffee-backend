package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.andThen
import eu.yeger.koffee.model.dto.map
import eu.yeger.koffee.model.dto.withResult
import eu.yeger.koffee.repository.ImageRepository
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.utility.IMAGE_DELETED_SUCCESSFULLY
import eu.yeger.koffee.utility.IMAGE_UPLOADED_SUCCESSFULLY
import eu.yeger.koffee.utility.validateProfileImageExists
import eu.yeger.koffee.utility.validateUserExists

/**
 * Default [ImageService] implementation.
 *
 * @author Jan Müller
 */
class DefaultImageService(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ImageService {

    override suspend fun getProfileImageByUserId(id: String): Result<ProfileImage> {
        return imageRepository
            .validateProfileImageExists(id)
            .andThen { Result.ok(it) }
    }

    override suspend fun getProfileImageTimestampByUserId(id: String): Result<Long> {
        return imageRepository
            .validateProfileImageExists(id)
            .andThen { Result.ok(it.timestamp) }
    }

    override suspend fun saveProfileImageForUser(id: String, image: String): Result<String> {
        return userRepository
            .validateUserExists(id)
            .map { user -> ProfileImage(user.id, image, System.currentTimeMillis()) }
            .withResult { imageRepository.insert(it) }
            .andThen { Result.created(IMAGE_UPLOADED_SUCCESSFULLY) }
    }

    override suspend fun deleteProfileImageByUserId(id: String): Result<String> {
        return imageRepository
            .validateProfileImageExists(id)
            .withResult { user -> imageRepository.removeByUserId(user.id) }
            .andThen { Result.ok(IMAGE_DELETED_SUCCESSFULLY) }
    }
}
