package eu.yeger.utility

import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import eu.yeger.model.domain.Entity
import kotlin.reflect.KProperty
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.path

suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T, options: UpdateOptions = UpdateOptions()) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = options.upsert(true)
    )

infix fun KProperty<Double>.incrementBy(amount: Number) = Updates.inc(this.path(), amount)

infix fun <T> KProperty<Iterable<T>?>.push(element: T) = Updates.push(this.path(), element)
