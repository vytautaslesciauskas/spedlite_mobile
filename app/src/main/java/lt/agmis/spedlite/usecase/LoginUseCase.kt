package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.network.LoginResponse
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.util.runCatchingCoroutine

class LoginUseCase(
    private val spedliteApiClient: SpedliteApiClient,
    private val spedliteSettings: SpedliteSettings
) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return runCatchingCoroutine {
            val response = spedliteApiClient.login(username, password)
            spedliteSettings.setToken(response.token)
            response
        }
            .onFailure { Napier.e("Failed to login", it) }
    }
}