package lt.agmis.spedlite.ui.destination.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import lt.agmis.spedlite.BuildConfig
import lt.agmis.spedlite.R
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.util.runCatchingCoroutine
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap
import lt.agmis.spedlite.ui.component.GapHalf
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTextField
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.PreviewDayNight
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.util.ExceptionMessageParser

@Composable
fun LoginDestination(appContainer: AppContainer) {
    val viewModel = viewModel<LoginViewModel>(factory = LoginViewModel.factory(appContainer))
    LoginScreen(onLoginClick = viewModel::onLoginClick, toggleAppTheme = viewModel::toggleAppTheme)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    toggleAppTheme: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SpedliteScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            topBar = {
                SpedliteTopAppBar(actions = {
                    DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
                })
            }) {
            Image(
                painter = painterResource(R.drawable.login_visual),
                contentDescription = null,
            )
            Gap()
            Text(text = stringResource(R.string.login_welcome), style = MaterialTheme.typography.headlineLarge)
            Text(text = stringResource(R.string.login_body))
            Gap()
            var username by remember { mutableStateOf(if (BuildConfig.DEBUG) "vytautas" else "") }
            var password by remember { mutableStateOf(if (BuildConfig.DEBUG) "1" else "") }
            SpedliteTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpedliteTheme.dimen.horizontalPadding),
                label = {
                    Text(text = stringResource(R.string.login_username))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            GapHalf()
            var passwordVisible by remember { mutableStateOf(false) }
            SpedliteTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpedliteTheme.dimen.horizontalPadding),
                label = {
                    Text(text = stringResource(R.string.login_password))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                suffix = {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.visibility_on else R.drawable.visibility_off),
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            onClick = {
                                passwordVisible = !passwordVisible
                            },
                            indication = ripple(bounded = false),
                            interactionSource = null
                        )
                    )
                }
            )

            GapHalf()
            SpedliteButton(
                onClick = {
                    onLoginClick(username, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpedliteTheme.dimen.horizontalPadding),
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text(text = stringResource(R.string.login_cta))
            }
        }
        Image(
            painter = painterResource(R.drawable.login_visual_bottom),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
@PreviewDayNight
private fun Preview() {
    SpedliteTheme {
        LoginScreen({ u, p -> }, {})
    }
}

class LoginViewModel(
    private val appNavigator: AppNavigator,
    private val dialogManager: DialogManager,
    private val settings: SpedliteSettings,
    private val apiClient: SpedliteApiClient,
    private val exceptionMessageParser: ExceptionMessageParser
) : ViewModel() {

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            initializer<LoginViewModel> {
                LoginViewModel(
                    appContainer.appNavigator,
                    appContainer.dialogManager,
                    appContainer.settings,
                    appContainer.apiClient,
                    appContainer.exceptionMessageParser,
                )
            }
        }
    }

    fun onLoginClick(username: String, password: String) {
        viewModelScope.launch {
            dialogManager.showProgressDialog()
            val result = runCatchingCoroutine {
                apiClient.login(username, password)
            }
            dialogManager.dismissProgressDialog()
            result.onSuccess {
                appNavigator.setRoot(Screen.Tasks())
            }
                .onFailure {
                    dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
                }
        }
    }

    fun toggleAppTheme(isLightMode: Boolean) {
        settings.setAppTheme(if (isLightMode) AppTheme.Light else AppTheme.Dark)
    }
}