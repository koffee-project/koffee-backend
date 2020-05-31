package eu.yeger.service

import eu.yeger.model.domain.ProfileImage
import eu.yeger.model.dto.Result

interface ImageService {

    suspend fun getProfileImageByUserId(id: String): Result<ProfileImage>

    suspend fun getProfileImageTimestampByUserId(id: String): Result<Long>

    suspend fun saveProfileImageForUser(id: String, image: String): Result<String>

    suspend fun deleteProfileImageByUserId(id: String): Result<String>
}
