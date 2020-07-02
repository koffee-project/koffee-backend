package eu.yeger.model.domain

/**
 * The profile image of a user.
 *
 * @property id The id of the image. Corresponds to a [User] id.
 * @property encodedImage The encoded bytes of the image.
 * @property timestamp The timestamp of the image's upload date.
 *
 * @author Jan Müller
 */
class ProfileImage(
    override val id: String,
    val encodedImage: String,
    val timestamp: Long
) : Entity
