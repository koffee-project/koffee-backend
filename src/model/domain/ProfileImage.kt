package eu.yeger.model.domain

data class ProfileImage(
    val userId: String,
    val encodedImage: String
) : Entity {
    override val id: String
        get() = userId
}
