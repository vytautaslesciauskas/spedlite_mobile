package lt.agmis.spedlite.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpStatement
import io.ktor.http.parameters
import kotlinx.serialization.Serializable
import lt.agmis.spedlite.settings.SpedliteSettings

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
    val refresh: String,
    val tasks: List<TaskDto>
)

@Serializable
data class TaskDto(
    val id: Long,
//    val cid: String,
//    val deviceId: String,
//    val licence: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val type: String,
//    val queue: Int,
    val visited: Long,
//    val client: String,
//    val con_id: String,
    val status: Int,
    val country: String? = null
)

@Serializable
data class StatusChangeResponse(
    val success: Boolean,
    val task: TaskDto
)

@Serializable
data class LocationUpdateResponse(
    val truck_id: Long,
    val lat: Double,
    val lng: Double
)

@Serializable
data class AppVersionResponse(
    val version: Int,
    val url: String
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


interface SpedliteApi {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun getTasks(): TasksResponse
    suspend fun changeTaskStatus(taskId: Long, status: Int): StatusChangeResponse
    suspend fun updateLocation(
        latitude: Double,
        longitude: Double,
        networkType: Int,
        overrideNetworkType: Int,
        timestampMillis: Long
    ): LocationUpdateResponse

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String = newPassword
    ): ChangePasswordResponse

    suspend fun logout()
    suspend fun getAppVersion(): AppVersionResponse
    suspend fun downloadApk(fileUrl: String): HttpStatement
}

class SpedliteApiClient(
    private val client: HttpClient,
    private val baseUrl: String,
    private val spedliteSettings: SpedliteSettings
) : SpedliteApi {

    private val token: String?
        get() = spedliteSettings.getToken()

    override suspend fun login(username: String, password: String): LoginResponse {
        val response = client.submitForm(
            url = "$baseUrl/auth.php",
            formParameters = parameters {
                append("username", username)
                append("password", password)
            }
        ).body<LoginResponse>()
        return response
    }

    override suspend fun getTasks(): TasksResponse {
        val token = token ?: throw RuntimeException("Token is null")
        return client.submitForm(
            url = "$baseUrl/getTasks.php",
            formParameters = parameters {
                append("token", token)
            }
        ).body()
    }

    override suspend fun changeTaskStatus(
        taskId: Long,
        status: Int
    ): StatusChangeResponse {
        val token = token ?: throw RuntimeException("Token is null")
        return client.submitForm(
            url = "$baseUrl/changeStatus.php",
            formParameters = parameters {
                append("token", token)
                append("task_id", taskId.toString())
                append("status", status.toString())
            }
        ).body()
    }

    override suspend fun updateLocation(
        latitude: Double,
        longitude: Double,
        networkType: Int,
        overrideNetworkType: Int,
        timestampMillis: Long
    ): LocationUpdateResponse {
        val token = token ?: throw RuntimeException("Token is null")
        return client.submitForm(
            url = "$baseUrl/updateTruckLocation.php",
            formParameters = parameters {
                append("token", token)
                append("lat", latitude.toString())
                append("lng", longitude.toString())
                append("source", networkType.toString())
                append("networkType", overrideNetworkType.toString())
                append("sent", timestampMillis.toString())
            }
        ).body()
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String
    ): ChangePasswordResponse {
        val token = token ?: throw RuntimeException("Token is null")
        val response = client.submitForm(
            url = "$baseUrl/auth_change.php",
            formParameters = parameters {
                append("token", token)
                append("old_password", oldPassword)
                append("new_password", newPassword)
                append("new_password_confirm", newPasswordConfirm)
            }
        ).body<ChangePasswordResponse>()
        return response
    }

    override suspend fun logout() {
        val token = token ?: throw RuntimeException("Token is null")
        client.submitForm(
            url = "$baseUrl/logout.php",
            formParameters = parameters {
                append("token", token)
            }
        )
    }

    override suspend fun getAppVersion(): AppVersionResponse {
        return client.get("$baseUrl/version.php").body<AppVersionResponse>()
    }

    override suspend fun downloadApk(fileUrl: String): HttpStatement {
        return client.prepareGet(
            urlString = fileUrl,
            block = {
                timeout {
                    requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                }
            }
        )
    }
}
