package eu.yeger.repository

import eu.yeger.model.domain.ProfileImage
import eu.yeger.utility.upsert
import org.litote.kmongo.coroutine.CoroutineDatabase

/**
 * [ImageRepository] based on MongoDB.
 *
 * @author Jan Müller
 */
class MongoImageRepository(database: CoroutineDatabase) : ImageRepository {

    private val imageCollection = database.getCollection<ProfileImage>()

    override suspend fun getByUserId(id: String): ProfileImage? {
        return imageCollection.findOneById(id)
    }

    override suspend fun insert(profileImage: ProfileImage) {
        imageCollection.upsert(entity = profileImage)
    }

    override suspend fun removeByUserId(id: String) {
        imageCollection.deleteOneById(id = id)
    }
}
