package eu.yeger.model

data class User(
    override val id: String,
    val name: String,
    val balance: Double
) : Entity()
