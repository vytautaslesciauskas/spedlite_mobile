package lt.agmis.spedlite.di

import android.app.Application
import androidx.compose.runtime.Stable
import lt.agmis.spedlite.BuildConfig
import lt.agmis.spedlite.EventDispatcher
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.usecase.ChangeTaskStatusUseCase
import lt.agmis.spedlite.usecase.FetchTasksUseCase
import lt.agmis.spedlite.usecase.LoginUseCase
import lt.agmis.spedlite.usecase.LogoutUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser

@Stable
class AppContainer(val application: Application) {
    val settings = SpedliteSettings(application)
    val appNavigator = AppNavigator(if (settings.getToken() != null) Screen.Tasks() else Screen.Login())
    val dialogManager = DialogManager()
    val eventDispatcher = EventDispatcher()
    val apiClient = SpedliteApiClient(eventDispatcher, BuildConfig.SERVER_URL + "/mobile", settings)
    val exceptionMessageParser = ExceptionMessageParser(application)

    val loginUseCase = LoginUseCase(apiClient, settings)
    val logoutUseCase = LogoutUseCase(apiClient, settings)
    val fetchTasksUseCase = FetchTasksUseCase(apiClient)
    val changeTaskStatusUseCase = ChangeTaskStatusUseCase(apiClient)

}