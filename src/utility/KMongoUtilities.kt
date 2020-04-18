package eu.yeger.utility

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import eu.yeger.model.domain.Entity
import kotlin.reflect.KProperty
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.path

suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T, options: UpdateOptions = UpdateOptions()) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = options.upsert(true)
    )

infix fun KProperty<Number?>.incrementBy(amount: Number): Bson = Updates.inc(this.path(), amount)

infix fun <T> KProperty<Iterable<T>?>.push(element: T): Bson = Updates.push(this.path(), element)

infix fun <T> KProperty<T>.to(newValue: T): Bson = Updates.set(this.path(), newValue)

fun List<Bson>.combineAsUpdate(): Bson = Updates.combine(this)

fun List<Bson>.combineAsFilter(): Bson = Filters.and(this)
