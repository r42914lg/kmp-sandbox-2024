package co.touchlab.kampkit.ktor

import co.touchlab.kampkit.response.BreedResult
import co.touchlab.kampkit.response.PictureResult
import co.touchlab.kampkit.utils.runOperationCatching
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json

class DogApiImpl(private val log: KermitLogger, engine: HttpClientEngine) : DogApi {

    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            logger = object : KtorLogger {
                override fun log(message: String) {
                    log.v { message }
                }
            }

            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }

    override suspend fun getDogs() = runOperationCatching {
        log.d { "Fetching Breeds from network" }
        client.get {
            dogs("api/breeds/list/all")
        }.body<BreedResult>()
    }

    override suspend fun getDogPicture(breed: String) = runOperationCatching {
        log.d { "Fetching Picture for dog from network" }
        client.get {
            dogs("api/breed/$breed/images/random")
        }.body<PictureResult>()
    }

    private fun HttpRequestBuilder.dogs(path: String) {
        url {
            takeFrom("https://dog.ceo/")
            encodedPath = path
        }
    }
}
