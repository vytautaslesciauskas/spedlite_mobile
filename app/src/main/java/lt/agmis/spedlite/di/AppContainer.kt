package lt.agmis.spedlite.di

import android.app.Application
import androidx.compose.runtime.Stable
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import lt.agmis.spedlite.BuildConfig
import lt.agmis.spedlite.Event
import lt.agmis.spedlite.EventDispatcher
import lt.agmis.spedlite.location.NetworkInfoProvider
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.BackendException
import lt.agmis.spedlite.network.ErrorBody
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.usecase.AppUpdateUseCase
import lt.agmis.spedlite.usecase.ChangeTaskStatusUseCase
import lt.agmis.spedlite.usecase.FetchTasksUseCase
import lt.agmis.spedlite.usecase.LoginUseCase
import lt.agmis.spedlite.usecase.LogoutUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser
import java.io.IOException

@Stable
class AppContainer(val application: Application) {
    val settings = SpedliteSettings(application)
    val appNavigator = AppNavigator(if (settings.getToken() != null) Screen.Tasks() else Screen.Login())
    val dialogManager = DialogManager()
    val eventDispatcher = EventDispatcher()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val client = HttpClient(OkHttp) {
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
                            settings.setToken(null)
                        }
                        throw exception
                    }
                }
            }
        }
    }

    val apiClient = SpedliteApiClient(client, BuildConfig.SERVER_URL + "/mobile", settings)
    val networkInfoProvider = NetworkInfoProvider(application)
    val exceptionMessageParser = ExceptionMessageParser(application)

    val loginUseCase = LoginUseCase(apiClient, settings)
    val logoutUseCase = LogoutUseCase(apiClient, settings, application)
    val fetchTasksUseCase = FetchTasksUseCase(apiClient, settings)
    val changeTaskStatusUseCase = ChangeTaskStatusUseCase(apiClient)

    val appUpdateUseCase = AppUpdateUseCase(apiClient, application)

}
