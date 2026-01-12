package lt.agmis.spedlite.ui.destination.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun SettingsDestination(appContainer: AppContainer) {
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModel.factory(appContainer))
    SettingsScreen(viewModel::toggleAppTheme)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    toggleAppTheme: (Boolean) -> Unit,
) {
    SpedliteScaffold(
        horizontalAlignment = Alignment.CenterHorizontally,
        topBar = {
            SpedliteTopAppBar(actions = {
                DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
            })
        }) {
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
    SettingsScreen({})
        }
}

class SettingsViewModel(
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val apiClient: SpedliteApiClient,
    private val exceptionMessageParser: ExceptionMessageParser
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            initializer<SettingsViewModel> {
                SettingsViewModel(
                    appContainer.appNavigator,
                    appContainer.dialogManager,
                    appContainer.settings,
                    appContainer.apiClient,
                    appContainer.exceptionMessageParser,
                )
            }
        }
    }

    fun toggleAppTheme(isLightMode: Boolean) {
        settings.setAppTheme(if (isLightMode) AppTheme.Light else AppTheme.Dark)
    }

}