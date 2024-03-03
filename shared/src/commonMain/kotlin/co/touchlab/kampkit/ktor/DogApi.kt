package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.response.BreedResult
import co.touchlab.kampkit.response.PictureResult
import co.touchlab.kampkit.utils.Result

interface DogApi {
    suspend fun getDogs(): Result<BreedResult, Throwable>
    suspend fun getDogPicture(breed: String): Result<PictureResult, Throwable>
}
