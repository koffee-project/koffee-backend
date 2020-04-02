package eu.yeger.model

data class Item(
    override val id: String,
    val name: String,
    val amount: Int,
    val price: Double
) : Entity()
