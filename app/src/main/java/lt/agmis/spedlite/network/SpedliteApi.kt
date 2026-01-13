package lt.agmis.spedlite.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import lt.agmis.spedlite.BuildConfig
import lt.agmis.spedlite.Event
import lt.agmis.spedlite.EventDispatcher
import java.io.IOException

@Serializable
data class LoginResponse(
    val token: String,
)

@Serializable
data class ChangePasswordResponse(
    val message: String,
)

@Serializable
data class TasksResponse(
    val licence: String,
    val count: Int,
    val tasks: List<TaskDto>
)

@Serializable
data class TaskDto(
    val id: String,
    val cid: String,
    val deviceId: String,
    val licence: String,
    val address: String,
    val lat: String,
    val lng: String,
    val type: String,         // LOADING / UNLOADING etc.
    val queue: String,
    val visited: String,      // timestamp as string (can be negative)
    val client: String,
    val con_id: String,
    val status: String        // "0" / "1" etc.
)

@Serializable
data class StatusChangeResponse(
    val task_id: Long,
    val status: Int
)

@Serializable
data class LocationUpdateResponse(
    val truck_id: Long,
    val lat: Double,
    val lng: Double
)

@Serializable
data class ErrorBody(
    val error: String
)

class BackendException(
    val code: Int,
    val body: ErrorBody?,
    val errorJson: String,
    path: String,
) : RuntimeException("Code: $code\nMessage: ${errorJson}\nAt path: $path")


class SpedliteApiClient(
    private val eventDispatcher: EventDispatcher,
    private val baseUrl: String
) {

    private var token: String? = null

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = HttpClient(OkHttp) {
        install(HttpRequestRetry) {
            noRetry()
            retryOnExceptionIf { http, exception ->
                Napier.w("Retrying request ${http.url}, exception $exception")
                exception is IOException
            }
            maxRetries = 3
        }
        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }
        expectSuccess = true
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.log(io.github.aakira.napier.LogLevel.VERBOSE, "KTOR", null, message)
                    }
                }
                level = LogLevel.BODY
                filter {
                    !it.url.pathSegments.contains("media-upload")
                }
            }
        }
        install(ContentNegotiation) {
            json(json)
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { ktorException, request ->
                val responseException = ktorException as? ResponseException
                    ?: return@handleResponseExceptionWithRequest
                val response = responseException.response
                when (val statusCode = response.status.value) {
                    in 400..599 -> {
                        val errorJson = response.bodyAsText()
                        val errorBody = runCatching { json.decodeFromString<ErrorBody>(errorJson) }
                            .onFailure { Napier.e("Failed to parse ErrorBody from $errorJson", it) }
                            .getOrNull()
                        val url = request.url.toString()
                        val exception = BackendException(statusCode, errorBody, errorJson, url)
                        if (response.status == HttpStatusCode.Unauthorized && !url.contains("auth_change")) {
                            eventDispatcher.tryEmit(Event.Unauthorized(errorBody))
                        }
                        throw exception
                    }
                }
            }
        }
    }

    suspend fun login(username: String, password: String): LoginResponse {
        val response = client.submitForm(
            url = "$baseUrl/auth.php",
            formParameters = parameters {
                append("username", username)
                append("password", password)
            }
        ).body<LoginResponse>()
        token = response.token
        return response
    }

    suspend fun getTasks(): TasksResponse {
        return client.submitForm(
            url = "$baseUrl/getTasks.php",
            formParameters = parameters {
                token?.let { append("token", it) }
            }
        ).body()
    }

    suspend fun changeTaskStatus(
        taskId: String,
        status: Int = 1
    ): StatusChangeResponse {
        return client.submitForm(
            url = "$baseUrl/changeStatus.php",
            formParameters = parameters {
                token?.let { append("token", it) }
                append("task_id", taskId)
                append("status", status.toString())
            }
        ).body()
    }


    suspend fun updateLocation(
        latitude: Double,
        longitude: Double
    ): LocationUpdateResponse {
        return client.submitForm(
            url = "$baseUrl/updateTruckLocation.php",
            formParameters = parameters {
                token?.let { append("token", it) }
                append("lat", latitude.toString())
                append("lng", longitude.toString())
            }
        ).body()
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String = newPassword
    ): ChangePasswordResponse {
        val response =  client.submitForm(
            url = "$baseUrl/auth_change.php",
            formParameters = parameters {
                token?.let { append("token", it) }
                append("old_password", oldPassword)
                append("new_password", newPassword)
                append("new_password_confirm", newPasswordConfirm)
            }
        ).body<ChangePasswordResponse>()
        return response
    }

    fun clearToken() {
        token = null
    }

}