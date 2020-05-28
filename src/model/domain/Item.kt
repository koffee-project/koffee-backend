package eu.yeger.model.domain

import java.util.UUID

data class Item(
    override val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Int?,
    val price: Double
) : Entity
