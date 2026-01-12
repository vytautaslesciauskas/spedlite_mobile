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
import lt.agmis.spedlite.ui.destination.tasks.FetchTasksUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser

@Stable
class AppContainer(val application: Application) {

    val appNavigator = AppNavigator(Screen.Login())
    val dialogManager = DialogManager()
    val settings = SpedliteSettings(application)
    val eventDispatcher = EventDispatcher()
    val apiClient = SpedliteApiClient(eventDispatcher, BuildConfig.SERVER_URL + "/mobile")
    val exceptionMessageParser = ExceptionMessageParser(application)

    val fetchTasksUseCase = FetchTasksUseCase(apiClient)

}