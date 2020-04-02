package eu.yeger.utility

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection

suspend fun <T : Any> CoroutineCollection<T>.upsert(id: String, entity: T) {
    updateOneById(id = id, update = entity, options = UpdateOptions().upsert(true))
}
