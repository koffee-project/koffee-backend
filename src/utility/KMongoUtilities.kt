package eu.yeger.utility

import com.mongodb.client.model.UpdateOptions
import eu.yeger.model.Entity
import org.litote.kmongo.coroutine.CoroutineCollection

suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T, options: UpdateOptions = UpdateOptions()) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = options.upsert(true)
    )
