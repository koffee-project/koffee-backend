package eu.yeger.koffee.model.domain

import java.util.UUID

/**
 * [Entity] class representing [Item]s.
 *
 * @property id The id of the [Item].
 * @property name The name of the [Item].
 * @property amount The optional amount of the [Item].
 * @property price The price of the [Item].
 *
 * @author Jan Müller
 */
data class Item(
    override val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Int?,
    val price: Double
) : Entity
