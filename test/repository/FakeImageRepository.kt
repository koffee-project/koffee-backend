package eu.yeger.repository

import eu.yeger.model.domain.ProfileImage

class FakeImageRepository : ImageRepository {

    private val profileImages = HashMap<String, ProfileImage>()

    override suspend fun getByUserId(id: String): ProfileImage? {
        return profileImages[id]
    }

    override suspend fun insert(profileImage: ProfileImage) {
        profileImages[profileImage.id] = profileImage
    }

    override suspend fun removeByUserId(id: String) {
        profileImages.remove(id)
    }
}
