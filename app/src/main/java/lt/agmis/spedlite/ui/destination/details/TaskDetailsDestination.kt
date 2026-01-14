package lt.agmis.spedlite.ui.destination.details

import android.content.Intent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import lt.agmis.spedlite.R
import lt.agmis.spedlite.SampleData
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.model.toStringRes
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.ConfirmDialog
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.navigation.toUIText
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap10
import lt.agmis.spedlite.ui.component.Gap2
import lt.agmis.spedlite.ui.component.Gap3
import lt.agmis.spedlite.ui.component.Gap5
import lt.agmis.spedlite.ui.component.SpedliteCard
import lt.agmis.spedlite.ui.component.SpedliteIconButton
import lt.agmis.spedlite.ui.component.SpedliteIconButtonRound
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTextButton
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.destination.tasks.ChangeTaskStatusButton
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.usecase.ChangeTaskStatusUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun TaskDetailsDestination(appContainer: AppContainer, task: Task) {
    val viewModel = viewModel<TaskDetailsViewModel>(factory = TaskDetailsViewModel.factory(appContainer, task))
    val context = LocalContext.current
    TaskDetailsScreen(
        task = viewModel.task,
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
        },
        onChangeStatusClick = viewModel::onChangeStatusClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsScreen(
    task: Task,
    toggleAppTheme: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    onOpenMapClick: (Double, Double) -> Unit,
    onChangeStatusClick: (Task) -> Unit
) {

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
                    val onBack = LocalOnBackPressedDispatcherOwner.current
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
                    val taskTypeName = task.type.toStringRes()?.let { stringResource(it) } ?: task.typeRaw
                    Text(text = taskTypeName, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 26.sp))
                    Gap5()
                    HorizontalDivider()
                    Gap5()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.task_details_title_to), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(0.26f))
                        Row(modifier = Modifier.weight(0.74f), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                val flagUnicodeEmoji = task.country?.let { countryCodeToFlagEmoji(it) }
                                if (flagUnicodeEmoji == null) {
                                    Icon(painter = painterResource(R.drawable.ic_anywhere), contentDescription = null)
                                } else {
                                    Text(text = flagUnicodeEmoji, fontSize = 24.sp)
                                }
                            }
                            Gap3()
                            Text(text = task.address)
                        }
                    }
                    Gap3()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.task_details_title_status), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(0.26f))
                        val statusText = task.getStatusText()
                        Row(modifier = Modifier.weight(0.74f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.ic_dot), contentDescription = null, tint = SpedliteTheme.colorScheme.success)
                            Gap3()
                            Text(text = statusText, modifier = Modifier.weight(0.74f))
                        }
                    }
                    Gap10()
                    ChangeTaskStatusButton(task.status, { onChangeStatusClick(task) }, modifier = Modifier.fillMaxWidth())
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

private fun countryCodeToFlagEmoji(countryCode: String): String? {
    if (countryCode.length != 2) return null

    val upperCode = countryCode.uppercase()
    if (!upperCode.all { it in 'A'..'Z' }) return null

    val firstChar = 0x1F1E6 + (upperCode[0] - 'A')
    val secondChar = 0x1F1E6 + (upperCode[1] - 'A')

    return String(intArrayOf(firstChar, secondChar), 0, 2)
}

@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
        TaskDetailsScreen(
            SampleData.singleTask,
            {},
            {},
            { s, g -> },
            {}
        )
    }
}

class TaskDetailsViewModel(
    task: Task,
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val exceptionMessageParser: ExceptionMessageParser,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val getString: (Int, Array<Any>) -> String,
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer, task: Task) = viewModelFactory {
            initializer<TaskDetailsViewModel> {
                TaskDetailsViewModel(
                    task,
                    appContainer.appNavigator,
                    appContainer.dialogManager,
                    appContainer.settings,
                    appContainer.exceptionMessageParser,
                    appContainer.changeTaskStatusUseCase,
                    { res, args -> appContainer.application.getString(res, *args) },
                )
            }
        }
    }

    var task by mutableStateOf(task)

    fun toggleAppTheme(isLightMode: Boolean) {
        settings.setAppTheme(if (isLightMode) AppTheme.Light else AppTheme.Dark)
    }

    fun onSettingsClick() {
        appNavigator.navigate(Screen.Settings())
    }

    fun onChangeStatusClick(task: Task) {
        dialogManager.showConfirmDialog(
            ConfirmDialog(
                message = task.getTaskMessageForChangingStatus(getString).toUIText(),
                positiveButtonText = R.string.common_yes.toUIText(),
                cancelButtonText = R.string.common_no.toUIText(),
                onConfirm = { changeStatus(task) }
            )
        )
    }

    private fun changeStatus(task: Task) {
        viewModelScope.launch {
            dialogManager.showProgressDialog()
            val result = changeTaskStatusUseCase.changeStatus(task.id, task.status)
            dialogManager.dismissProgressDialog()
            result.onSuccess { response ->
                this@TaskDetailsViewModel.task = task.copy(statusRaw = response.status)
            }.onFailure {
                dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
            }
        }
    }

}