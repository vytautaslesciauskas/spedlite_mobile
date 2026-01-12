package lt.agmis.spedlite.ui.destination.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import lt.agmis.spedlite.R
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteCard
import lt.agmis.spedlite.ui.component.SpedliteIcon
import lt.agmis.spedlite.ui.component.SpedliteIconButtonRound
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun TasksDestination(appContainer: AppContainer) {
    val viewModel = viewModel<TasksViewModel>(factory = TasksViewModel.factory(appContainer))
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }
    TasksScreen(
        toggleAppTheme = viewModel::toggleAppTheme,
        onSettingsClick = viewModel::onSettingsClick,
        isLoading = viewModel.isLoading,
        onRefresh = viewModel::loadTasks,
        tasks = viewModel.tasks,
        onTaskClick = viewModel::onTaskClick
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
) {
    PullToRefreshBox(isRefreshing = isLoading, onRefresh = onRefresh, modifier = Modifier.fillMaxSize()) {
        SpedliteScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            topBar = {
                SpedliteTopAppBar(actions = {
                    DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
                    SpedliteIconButtonRound(onClick = onSettingsClick) {
                        Icon(painter = painterResource(R.drawable.ic_settings), contentDescription = null)
                    }
                })
            }) {
            tasks.forEach { task -> TaskItem(task = task, onClick = { onTaskClick(task) }) }
        }
    }
}

@Composable
private fun TaskItem(task: Task, onClick: () -> Unit) {
    SpedliteCard(modifier = Modifier.padding(horizontal = SpedliteTheme.dimen.horizontalPadding), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpedliteIcon(R.drawable.ic_truck)
            Gap()
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.type, style = MaterialTheme.typography.titleMedium)
                Text(text = task.visited, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB1B2B4))
            }
            Gap()
            SpedliteButton(onClick = {}) {
                Text(text = "Begin")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
        TasksScreen({}, {}, false, {}, emptyList(), {})
    }
}

class TasksViewModel(
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val apiClient: SpedliteApiClient,
    private val exceptionMessageParser: ExceptionMessageParser,
    private val fetchTasksUseCase: FetchTasksUseCase
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            initializer<TasksViewModel> {
                TasksViewModel(
                    appContainer.appNavigator,
                    appContainer.dialogManager,
                    appContainer.settings,
                    appContainer.apiClient,
                    appContainer.exceptionMessageParser,
                    appContainer.fetchTasksUseCase,
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
}