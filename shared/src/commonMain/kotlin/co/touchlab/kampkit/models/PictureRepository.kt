package co.touchlab.kampkit.models

import co.touchlab.kampkit.ktor.DogApi
import co.touchlab.kampkit.response.PictureResult
import co.touchlab.kampkit.utils.Result

class PictureRepository(
    private val dogApi: DogApi,
) {
    suspend fun getDogPicture(breed: String): Result<PictureResult, Throwable> =
        dogApi.getDogPicture(breed)
}
