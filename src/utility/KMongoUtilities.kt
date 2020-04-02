package eu.yeger.utility

import com.mongodb.client.model.UpdateOptions
import eu.yeger.model.Entity
import org.litote.kmongo.coroutine.CoroutineCollection

suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = UpdateOptions().upsert(true)
    )
