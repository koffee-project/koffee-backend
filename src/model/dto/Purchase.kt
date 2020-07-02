package eu.yeger.model.dto

import eu.yeger.model.domain.Item

/**
 * DTO class for purchases
 *
 * @property itemId The id of the purchased [Item].
 * @property amount The amount purchased.
 *
 * @author Jan Müller
 */
data class Purchase(
    val itemId: String,
    val amount: Int
)
