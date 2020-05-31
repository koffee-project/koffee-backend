package eu.yeger.model.domain

class ProfileImage(
    override val id: String,
    val encodedImage: String,
    val timestamp: Long
) : Entity
