package eu.yeger.service

import eu.yeger.model.dto.Result

interface ImageService {

    suspend fun getProfileImageByUserId(id: String): Result<ByteArray>

    suspend fun saveProfileImageForUser(id: String, image: ByteArray): Result<String>

    suspend fun deleteProfileImageByUserId(id: String): Result<String>
}
