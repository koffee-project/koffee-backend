package eu.yeger.koffee.repository

import eu.yeger.koffee.model.domain.ProfileImage
import eu.yeger.koffee.model.domain.User

/**
 * Repository for [ProfileImage]s.
 *
 * @author Jan Müller
 */
interface ImageRepository {

    /**
     * Retrieves a [ProfileImage] of a [User] from the eu.yeger.koffee.repository.
     *
     * @param id The id of the [User].
     * @return The [User]'s [ProfileImage] if available.
     */
    suspend fun getByUserId(id: String): ProfileImage?

    /**
     * Inserts a [ProfileImage] into the eu.yeger.koffee.repository.
     *
     * @param profileImage The [ProfileImage] to be inserted.
     */
    suspend fun insert(profileImage: ProfileImage)

    /**
     * Removed a [ProfileImage] from the eu.yeger.koffee.repository.
     *
     * @param id The id of the [ProfileImage] to be removed.
     */
    suspend fun removeByUserId(id: String)
}
