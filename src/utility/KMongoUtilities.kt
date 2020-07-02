package eu.yeger.utility

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import eu.yeger.model.domain.Entity
import kotlin.reflect.KProperty
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.path

/**
 * Upserts an [Entity].
 *
 * @param T The type of the [Entity].
 * @param entity The [Entity] to be upserted.
 * @param options The options for upserting.
 *
 * @author Jan Müller
 */
suspend fun <T : Entity> CoroutineCollection<T>.upsert(entity: T, options: UpdateOptions = UpdateOptions()) =
    updateOneById(
        id = entity.id,
        update = entity,
        options = options.upsert(true)
    )

/**
 * Creates an update Bson for incrementing numbers.
 *
 * @param amount The amount to increment by.
 * @return The update bson.
 *
 * @author Jan Müller
 */
infix fun KProperty<Number?>.incrementBy(amount: Number): Bson = Updates.inc(this.path(), amount)

/**
 * Creates an update Bson for pushing elements.
 *
 * @param T The type of the element.
 * @param element The element to be pushed.
 * @return The update bson.
 *
 * @author Jan Müller
 */
infix fun <T> KProperty<Iterable<T>?>.push(element: T): Bson = Updates.push(this.path(), element)

/**
 * Creates an update Bson.
 *
 * @param T The type of the value.
 * @param newValue The value to be set.
 * @return The update bson.
 *
 * @author Jan Müller
 */
infix fun <T> KProperty<T>.to(newValue: T): Bson = Updates.set(this.path(), newValue)

/**
 * Combines a list of Bsons with update semantics.
 *
 * @return The update Bson.
 *
 * @author Jan Müller
 */
fun List<Bson>.combineAsUpdate(): Bson = Updates.combine(this)

/**
 * Creates a filter from a list of Bsons with AND semantics.
 *
 * @return The filter Bson.
 *
 * @author Jan Müller
 */
fun List<Bson>.combineAsFilter(): Bson = Filters.and(this)
