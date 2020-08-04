package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.dto.Result

/**
 * Service for [ProfileImage]s.
 *
 * @author Jan Müller
 */
interface ProfileImageService {

    /**
     * Returns the [ProfileImage] of a [User].
     * Must validate that the [ProfileImage] exists.
     *
     * @param userId The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getProfileImageByUserId(userId: String): Result<ProfileImage>

    /**
     * Returns the timestamp of a [User]'s [ProfileImage].
     * Must validate that the [ProfileImage] exists.
     *
     * @param userId The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getProfileImageTimestampByUserId(userId: String): Result<Long>

    /**
     * Saves the [ProfileImage] of a [User].
     * Must validate that the [User] exists.
     *
     * @param userId The id of the [User].
     * @param image The encoded bytes of the image.
     * @return The [Result] of the operation.
     */
    suspend fun saveProfileImageForUser(userId: String, image: String): Result<String>

    /**
     * Deletes the [ProfileImage] of a [User].
     * Must validate that the [ProfileImage] exists.
     *
     * @param userId The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun deleteProfileImageByUserId(userId: String): Result<String>
}
