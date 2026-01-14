package lt.agmis.spedlite

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.navigation.UIText
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.Gap5
import lt.agmis.spedlite.ui.component.Gap6
import lt.agmis.spedlite.ui.component.SpedliteButtonError
import lt.agmis.spedlite.ui.component.SpedliteButtonSuccess
import lt.agmis.spedlite.ui.destination.details.TaskDetailsDestination
import lt.agmis.spedlite.ui.destination.login.LoginDestination
import lt.agmis.spedlite.ui.destination.settings.SettingsDestination
import lt.agmis.spedlite.ui.destination.tasks.TasksDestination
import lt.agmis.spedlite.ui.theme.SpedliteTheme

class MainActivity : ComponentActivity() {

    private lateinit var appNavigator: AppNavigator
    private lateinit var dialogManager: DialogManager
    private lateinit var spedliteSettings: SpedliteSettings

    var isDarkTheme by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = getAppContainer()
        appNavigator = appContainer.appNavigator
        dialogManager = appContainer.dialogManager
        spedliteSettings = appContainer.settings
        isDarkTheme = when (spedliteSettings.getAppTheme()) {
            AppTheme.Light -> false
            AppTheme.Dark -> true
            AppTheme.Auto -> {
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SpedliteTheme(darkTheme = isDarkTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Navigation(backStack = appNavigator.backStack, onBack = { appNavigator.back() }, appContainer = getAppContainer())
                }
                DialogContainer(dialogManager)
            }
        }

        appContainer.eventDispatcher.events
            .filterIsInstance<Event.Unauthorized>()
            .onEach { event ->
                if (appNavigator.backStack.lastOrNull() !is Screen.Login) {
                    appNavigator.setRoot(Screen.Login())
                    dialogManager.showInfoDialog(InfoDialog(event.errorBody?.error ?: "Unauthorized"))
                }
            }
            .launchIn(AppScope)
    }

    fun getAppContainer(): AppContainer {
        return (application as App).appContainer
    }
}

private const val DEFAULT_TRANSITION_DURATION_MILLISECOND = 500
private val IosTransitionEasing = CubicBezierEasing(0.2833f, 0.99f, 0.31833f, 0.99f)
private val veilColor = Color.Black.copy(alpha = 0.1f)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun Navigation(
    backStack: SnapshotStateList<Screen>,
    onBack: () -> Unit,
    appContainer: AppContainer
) {
    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = {
            onBack()
        },
        entryProvider = { key ->
            when (key) {
                is Screen.Login -> {
                    NavEntry(key) {
                        LoginDestination(appContainer)
                    }
                }

                is Screen.Tasks -> {
                    NavEntry(key) {
                        TasksDestination(appContainer)
                    }
                }

                is Screen.Settings -> {
                    NavEntry(key) {
                        SettingsDestination(appContainer)
                    }
                }

                is Screen.TaskDetails -> {
                    NavEntry(key) {
                        TaskDetailsDestination(appContainer, key.task)
                    }
                }
            }
        },
        transitionSpec = {
            ContentTransform(
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing),
                ),
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    targetOffset = { it / 4 },
                    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing),
                ) + veilOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing), targetColor = veilColor),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    initialOffset = { it / 4 },
                    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing),
                ) + unveilIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing), initialColor = veilColor),
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND, easing = IosTransitionEasing),
                ),
            )
        },
        predictivePopTransitionSpec = { edge ->
//            val towards = if (edge == EDGE_LEFT) {
//                AnimatedContentTransitionScope.SlideDirection.Right
//            } else {
//                AnimatedContentTransitionScope.SlideDirection.Left
//            }
            ContentTransform(
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    initialOffset = { it / 4 },
                    animationSpec = tween(300, easing = LinearEasing),
                ) + unveilIn(animationSpec = tween(300, easing = LinearEasing), initialColor = veilColor),
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = LinearEasing),
                ),
            )
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogContainer(dialogManager: DialogManager) {
    val infoDialog = dialogManager.infoDialog
    if (infoDialog != null) {
        AlertDialog(
            onDismissRequest = dialogManager::dismissInfoDialog,
            confirmButton = {
                TextButton(
                    onClick = dialogManager::dismissInfoDialog,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) { Text(text = stringResource(R.string.common_ok)) }
            },
            text = {
                val text = when (infoDialog.message) {
                    is UIText.RawString -> infoDialog.message.value
                    is UIText.Resource -> stringResource(infoDialog.message.resId)
                }
                Text(text = text)
            },
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    val confirmDialog = dialogManager.confirmDialog
    if (confirmDialog != null) {
        Dialog(
            onDismissRequest = dialogManager::dismissConfirmDialog,
            properties = DialogProperties(),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = SpedliteTheme.dimen.gap5, vertical = SpedliteTheme.dimen.gap6),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val text = when (confirmDialog.message) {
                        is UIText.RawString -> confirmDialog.message.value
                        is UIText.Resource -> stringResource(confirmDialog.message.resId)
                    }
                    Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 26.sp))
                    Gap6()
                    Row {
                        SpedliteButtonError(onClick = {
                            dialogManager.dismissConfirmDialog()
                            confirmDialog.onCancel?.invoke()
                        }) {
                            val text = when (confirmDialog.cancelButtonText) {
                                is UIText.RawString -> confirmDialog.cancelButtonText.value
                                is UIText.Resource -> stringResource(confirmDialog.cancelButtonText.resId)
                            }
                            Text(text = text)
                        }
                        Gap5()
                        SpedliteButtonSuccess(onClick = {
                            dialogManager.dismissConfirmDialog()
                            confirmDialog.onConfirm()
                        }) {
                            val text = when (confirmDialog.positiveButtonText) {
                                is UIText.RawString -> confirmDialog.positiveButtonText.value
                                is UIText.Resource -> stringResource(confirmDialog.positiveButtonText.resId)
                            }
                            Text(text = text)
                        }
                    }
                }
            }
        }
    }
    if (dialogManager.progressDialog) {
        BasicAlertDialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier,
                shape = AlertDialogDefaults.shape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Gap6()
                    Text("Loading...")
                }
            }
        }
    }
}

