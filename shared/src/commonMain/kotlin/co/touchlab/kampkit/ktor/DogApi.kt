package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.response.BreedResult
import co.touchlab.kampkit.utils.Result

interface DogApi {
    suspend fun getJsonFromApi(): Result<BreedResult, Throwable>
}
