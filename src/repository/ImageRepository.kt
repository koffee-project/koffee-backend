package eu.yeger.repository

import eu.yeger.model.domain.ProfileImage

interface ImageRepository {

    suspend fun getByUserId(id: String): ProfileImage?

    suspend fun insert(profileImage: ProfileImage)

    suspend fun removeByUserId(id: String)
}
