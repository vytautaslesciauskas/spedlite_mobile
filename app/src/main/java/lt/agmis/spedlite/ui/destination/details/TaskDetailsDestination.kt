package lt.agmis.spedlite.ui.destination.details

import android.content.Intent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.aakira.napier.Napier
import lt.agmis.spedlite.R
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap10
import lt.agmis.spedlite.ui.component.Gap2
import lt.agmis.spedlite.ui.component.Gap3
import lt.agmis.spedlite.ui.component.Gap5
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteCard
import lt.agmis.spedlite.ui.component.SpedliteIconButton
import lt.agmis.spedlite.ui.component.SpedliteIconButtonRound
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTextButton
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun TaskDetailsDestination(appContainer: AppContainer, task: Task) {
    val viewModel = viewModel<TaskDetailsViewModel>(factory = TaskDetailsViewModel.factory(appContainer))
    val context = LocalContext.current
    TaskDetailsScreen(
        task = task,
        toggleAppTheme = viewModel::toggleAppTheme,
        onSettingsClick = viewModel::onSettingsClick,
        onOpenMapClick = { latitude, longitude ->
            try {
                val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()

                val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

                // Check if there's an app that can handle this intent
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            } catch (exception: Exception) {
                Napier.e("Failed to open map", exception)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsScreen(
    task: Task,
    toggleAppTheme: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    onOpenMapClick: (String, String) -> Unit,
) {
    val onBack = LocalOnBackPressedDispatcherOwner.current
    SpedliteScaffold(
        horizontalAlignment = Alignment.CenterHorizontally,
        topBar = {
            SpedliteTopAppBar(
                actions = {
                    DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
                    SpedliteIconButtonRound(onClick = onSettingsClick) {
                        Icon(painter = painterResource(R.drawable.ic_settings), contentDescription = null)
                    }
                },
                navigationIcon = {
                    SpedliteIconButton(onClick = {
                        onBack?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
                    }
                },
                title = {
                    Text(stringResource(R.string.task_details_title))
                })
        }) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            SpedliteCard(
                modifier = Modifier.padding(horizontal = SpedliteTheme.dimen.gridSize * 5),
                contentPadding = PaddingValues(SpedliteTheme.dimen.gridSize * 5),
            ) {
                Column {
                    Text(text = task.type, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 26.sp))
                    Gap5()
                    HorizontalDivider()
                    Gap5()
                    Row {
                        Text(text = "To", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(0.26f))
                        Text(text = task.address, modifier = Modifier.weight(0.74f))
                    }
                    Gap3()
                    Row {
                        Text(text = "Status", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(0.26f))
                        Text(text = task.status, modifier = Modifier.weight(0.74f))
                    }
                    Gap10()
                    SpedliteButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Begin") }
                    Gap2()
                    SpedliteTextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { onOpenMapClick(task.lat, task.lng) }
                    ) {
                        Text(text = stringResource(R.string.task_details_open_map), textDecoration = TextDecoration.Underline)
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
        TaskDetailsScreen(
            Task("", "", "", "", "", "", "", "Delivery", "", "", "", "", ""),
            {},
            {},
            { s, g -> }
        )
    }
}

class TaskDetailsViewModel(
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val apiClient: SpedliteApiClient,
    private val exceptionMessageParser: ExceptionMessageParser
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            initializer<TaskDetailsViewModel> {
                TaskDetailsViewModel(
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

    fun onSettingsClick() {
        appNavigator.navigate(Screen.Settings())
    }
}