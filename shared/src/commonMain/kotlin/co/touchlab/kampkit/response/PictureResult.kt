package co.touchlab.kampkit.response

import kotlinx.serialization.Serializable

@Serializable
data class PictureResult(
    val message: String,
    var status: String
)
