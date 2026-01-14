package lt.agmis.spedlite.ui.destination.tasks

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import lt.agmis.spedlite.R
import lt.agmis.spedlite.SampleData
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.location.LocationService
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.model.TaskStatus
import lt.agmis.spedlite.model.toIconRes
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
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteButtonError
import lt.agmis.spedlite.ui.component.SpedliteButtonSuccess
import lt.agmis.spedlite.ui.component.SpedliteIcon
import lt.agmis.spedlite.ui.component.SpedliteIconButtonRound
import lt.agmis.spedlite.ui.component.SpedliteListItem
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.usecase.ChangeTaskStatusUseCase
import lt.agmis.spedlite.usecase.FetchTasksUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun TasksDestination(appContainer: AppContainer) {
    val viewModel = viewModel<TasksViewModel>(factory = TasksViewModel.factory(appContainer))
    val context = LocalContext.current

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (fineLocationGranted) {
            LocationService.start(context)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        permissionsLauncher.launch(permissions.toTypedArray())
    }
    TasksScreen(
        toggleAppTheme = viewModel::toggleAppTheme,
        onSettingsClick = viewModel::onSettingsClick,
        isLoading = viewModel.isLoading,
        onRefresh = viewModel::loadTasks,
        tasks = viewModel.tasks,
        onTaskClick = viewModel::onTaskClick,
        onChangeStatusClick = viewModel::onChangeStatusClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(
    toggleAppTheme: (Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onChangeStatusClick: (Task) -> Unit,
) {
    PullToRefreshBox(isRefreshing = isLoading, onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
        SpedliteScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpedliteTheme.dimen.gridSize * 2),
            topBar = {
                SpedliteTopAppBar(actions = {
                    DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
                    SpedliteIconButtonRound(onClick = onSettingsClick) {
                        Icon(painter = painterResource(R.drawable.ic_settings), contentDescription = null)
                    }
                })
            }) {
            tasks.forEach { task -> TaskItem(task = task, onClick = { onTaskClick(task) }, onChangeStatusClick = { onChangeStatusClick(task) }) }
        }
    }
}

@Composable
private fun TaskItem(task: Task, onClick: () -> Unit, onChangeStatusClick: () -> Unit) {
    SpedliteListItem(
        onClick = onClick,
        headlineContent = {
            val text = task.type.toStringRes()?.let { stringResource(it) } ?: task.typeRaw
            Text(text = text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpedliteTheme.dimen.gap5),
        supportingContent = {
            Text(text = task.dateTime)
        },
        trailingContent = {
            ChangeTaskStatusButton(task.status, onChangeStatusClick, modifier = Modifier.widthIn(min = 100.dp))
        },
        leadingContent = {
            val icon = task.type.toIconRes()
            SpedliteIcon(icon)
        }
    )
}

@Composable
fun ChangeTaskStatusButton(taskStatus: TaskStatus, onClick: () -> Unit, modifier: Modifier) {
    when (taskStatus) {
        TaskStatus.Pending -> {
            SpedliteButton(modifier = modifier, onClick = onClick) {
                Text(text = stringResource(R.string.common_task_begin))
            }
        }

        TaskStatus.InProgress -> {
            SpedliteButtonSuccess(modifier = modifier, onClick = onClick) {
                Text(text = stringResource(R.string.common_task_finish))
            }
        }

        TaskStatus.Finished -> {
            SpedliteButtonError(modifier = modifier, onClick = onClick) {
                Text(text = stringResource(R.string.common_task_restart))
            }
        }

        TaskStatus.Aborted -> {
            SpedliteButtonError(modifier = modifier, onClick = onClick) {
                Text(text = stringResource(R.string.common_task_restart))
            }
        }

        TaskStatus.Unknown -> {
            SpedliteButtonError(modifier = modifier, onClick = onClick) {
                Text(text = stringResource(R.string.common_task_restart))
            }
        }
    }

}

@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
        TasksScreen({}, {}, false, {}, SampleData.tasks, {}, {})
    }
}

class TasksViewModel(
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val exceptionMessageParser: ExceptionMessageParser,
    private val fetchTasksUseCase: FetchTasksUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val getString: (Int, Array<Any>) -> String
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            initializer<TasksViewModel> {
                TasksViewModel(
                    appContainer.appNavigator,
                    appContainer.dialogManager,
                    appContainer.settings,
                    appContainer.exceptionMessageParser,
                    appContainer.fetchTasksUseCase,
                    appContainer.changeTaskStatusUseCase,
                    { res, args -> appContainer.application.getString(res, *args) },
                )
            }
        }
    }

    val tasks = mutableStateListOf<Task>()
    var isLoading by mutableStateOf(false)

    init {
        loadTasks()
    }

    fun toggleAppTheme(isLightMode: Boolean) {
        settings.setAppTheme(if (isLightMode) AppTheme.Light else AppTheme.Dark)
    }

    fun onSettingsClick() {
        appNavigator.navigate(Screen.Settings())
    }

    fun loadTasks() {
        viewModelScope.launch {
            isLoading = true
            val result = fetchTasksUseCase.fetchTasks()
            isLoading = false
            result.onSuccess { newTasks ->
                tasks.clear()
                tasks.addAll(newTasks)
            }
                .onFailure {
                    dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
                }
        }
    }

    fun onTaskClick(task: Task) {
        appNavigator.navigate(Screen.TaskDetails(task))
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
                val updatedTasks = tasks.map { oldTask ->
                    if (oldTask.id == response.task_id) {
                        oldTask.copy(statusRaw = response.status)
                    } else {
                        oldTask
                    }
                }
                tasks.clear()
                tasks.addAll(updatedTasks)
            }.onFailure {
                dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
            }
        }
    }
}