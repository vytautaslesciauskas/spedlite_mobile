package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.util.runCatchingCoroutine

class LogoutUseCase(
    private val spedliteApiClient: SpedliteApiClient,
    private val spedliteSettings: SpedliteSettings
) {
    suspend fun logout(): Result<Unit> {
        return runCatchingCoroutine {
            val response = spedliteApiClient.logout()
            spedliteSettings.setToken(null)
            response
        }
            .onFailure { Napier.e("Failed to logout", it) }
    }
}