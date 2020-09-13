package eu.yeger.koffee.model.domain

/**
 * The profile image of a user.
 *
 * @property encodedImage The encoded bytes of the image.
 * @property timestamp The timestamp of the image's upload date.
 *
 * @author Jan Müller
 */
data class ProfileImage(
    val encodedImage: String,
    val timestamp: Long
)
