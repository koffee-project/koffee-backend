package eu.yeger.model.domain

class ProfileImage(
    val userId: String,
    val encodedImage: ByteArray
) : Entity {
    override val id: String
        get() = userId
}
