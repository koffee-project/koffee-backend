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
import org.bson.internal.Base64

class DefaultImageService(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ImageService {

    override suspend fun getProfileImageByUserId(id: String): Result<ByteArray> {
        return imageRepository.validateProfileImageExists(id)
            .map { profileImage -> Base64.decode(profileImage.encodedImage) }
            .andThen { Result.ok(it) }
    }

    override suspend fun saveProfileImageForUser(id: String, encodedImage: String): Result<String> {
        return userRepository.validateUserExists(id)
            .map { user -> ProfileImage(user.id, encodedImage) }
            .withResult { image -> imageRepository.insert(image) }
            .andThen { Result.created(IMAGE_UPLOADED_SUCCESSFULLY) }
    }

    override suspend fun deleteProfileImageByUserId(id: String): Result<String> {
        return imageRepository.validateProfileImageExists(id)
            .withResult { user -> imageRepository.removeByUserId(user.id) }
            .andThen { Result.ok(IMAGE_DELETED_SUCCESSFULLY) }
    }
}
