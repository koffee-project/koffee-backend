package eu.yeger.utility

import com.mongodb.client.model.UpdateOptions
import eu.yeger.model.Entity
import kotlin.reflect.KProperty
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.inc

suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T, options: UpdateOptions = UpdateOptions()) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = options.upsert(true)
    )

infix fun KProperty<Double>.incrementBy(amount: Number) = inc(this, amount)
