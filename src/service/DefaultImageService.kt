package eu.yeger.service

import eu.yeger.model.domain.ProfileImage
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.andThen
import eu.yeger.model.dto.map
import eu.yeger.model.dto.withResult
import eu.yeger.repository.ImageRepository
import eu.yeger.repository.UserRepository
import eu.yeger.utility.IMAGE_DELETED_SUCCESSFULLY
import eu.yeger.utility.IMAGE_UPLOADED_SUCCESSFULLY
import eu.yeger.utility.validateProfileImageExists
import eu.yeger.utility.validateUserExists

class DefaultImageService(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ImageService {

    override suspend fun getProfileImageByUserId(id: String): Result<ProfileImage> {
        return imageRepository.validateProfileImageExists(id)
            .andThen { Result.ok(it) }
    }

    override suspend fun getProfileImageTimestampByUserId(id: String): Result<Long> {
        return imageRepository.validateProfileImageExists(id)
            .andThen { Result.ok(it.timestamp) }
    }

    override suspend fun saveProfileImageForUser(id: String, image: String): Result<String> {
        return userRepository.validateUserExists(id)
            .map { user -> ProfileImage(user.id, image, System.currentTimeMillis()) }
            .withResult { imageRepository.insert(it) }
            .andThen { Result.created(IMAGE_UPLOADED_SUCCESSFULLY) }
    }

    override suspend fun deleteProfileImageByUserId(id: String): Result<String> {
        return imageRepository.validateProfileImageExists(id)
            .withResult { user -> imageRepository.removeByUserId(user.id) }
            .andThen { Result.ok(IMAGE_DELETED_SUCCESSFULLY) }
    }
}
