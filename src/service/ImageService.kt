package eu.yeger.service

import eu.yeger.model.domain.ProfileImage
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Result

/**
 * Service for [ProfileImage]s.
 *
 * @author Jan Müller
 */
interface ImageService {

    /**
     * Returns the [ProfileImage] of a [User].
     * Must validate that the [ProfileImage] exists.
     *
     * @param id The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getProfileImageByUserId(id: String): Result<ProfileImage>

    /**
     * Returns the timestamp of a [User]'s [ProfileImage].
     * Must validate that the [ProfileImage] exists.
     *
     * @param id The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getProfileImageTimestampByUserId(id: String): Result<Long>

    /**
     * Saves the [ProfileImage] of a [User].
     * Must validate that the [User] exists.
     *
     * @param id The id of the [User].
     * @param image The encoded bytes of the image.
     * @return The [Result] of the operation.
     */
    suspend fun saveProfileImageForUser(id: String, image: String): Result<String>

    /**
     * Deletes the [ProfileImage] of a [User].
     * Must validate that the [ProfileImage] exists.
     *
     * @param id The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun deleteProfileImageByUserId(id: String): Result<String>
}
