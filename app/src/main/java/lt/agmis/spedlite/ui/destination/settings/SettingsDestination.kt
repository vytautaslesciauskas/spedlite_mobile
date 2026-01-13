package lt.agmis.spedlite.ui.destination.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import lt.agmis.spedlite.R
import lt.agmis.spedlite.di.AppContainer
import lt.agmis.spedlite.navigation.AppNavigator
import lt.agmis.spedlite.navigation.ConfirmDialog
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap2
import lt.agmis.spedlite.ui.component.Gap3
import lt.agmis.spedlite.ui.component.Gap5
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteCard
import lt.agmis.spedlite.ui.component.SpedliteIcon
import lt.agmis.spedlite.ui.component.SpedliteListItem
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTextFieldPassword
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.util.ExceptionMessageParser
import lt.agmis.spedlite.util.runCatchingCoroutine

private val privacyPolicyHtml = """
                    <p>Šioje Privatumo politikoje naudojamos sąvokos ir sutrumpinimai turi šias reikšmes:</p>
                    <li><b>Asmens duomenys</b> – bet kokia informacija, susijusi su fiziniu asmeniu, kurį galima tiesiogiai arba netiesiogiai identifikuoti (pvz. vardas, pavardė, kontaktiniai duomenys ir kt.).</li>
                    <li><b>Asmens duomenys</b> – bet kokia informacija, susijusi su fiziniu asmeniu, kurį galima tiesiogiai arba netiesiogiai identifikuoti (pvz. vardas, pavardė, kontaktiniai duomenys ir kt.).</li>
                    <li><b>Asmuo</b> – fizinis asmuo (duomenų subjektas), kurio duomenys yra tvarkomi (pvz. Bendrovės Klientai, asmenys, kurie kreipiasi į Bendrovę, teikdami prašymus, reikalavimus, Bendrovės interneto svetainės, savitarnos svetainės ir kitų Bendrovės valdomų puslapių/mobiliųjų programėlių naudotojai ir kt.).</li>
                    <li><b>Duomenų tvarkymas</b> – bet kuris su Asmens duomenimis atliekamas veiksmas (pvz. rinkimas, įrašymas, saugojimas, prieigos suteikimas, perdavimas ir kt.).</li>
                    <li><b>Paslaugos</b> – bet kokios Bendrovės teikiamos prekės ir paslaugos.</li>
                """.trimIndent()

@Composable
fun SettingsDestination(appContainer: AppContainer) {
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModel.factory(appContainer))
    SettingsScreen(
        toggleAppTheme = viewModel::toggleAppTheme,
        onLogoutClick = viewModel::onLogoutClick,
        onChangePasswordClick = viewModel::onChangePassword,
        passwordChangedSuccessfully = viewModel.passwordChangedSuccessfully,
        oldPassword = viewModel.oldPassword,
        onOldPasswordChange = viewModel::onOldPasswordChange,
        newPassword = viewModel.newPassword,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        newPasswordRepeat = viewModel.newPasswordRepeat,
        onNewPasswordRepeatChange = viewModel::onNewPasswordRepeatChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    toggleAppTheme: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    passwordChangedSuccessfully: Boolean,
    oldPassword: String,
    onOldPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    newPasswordRepeat: String,
    onNewPasswordRepeatChange: (String) -> Unit
) {
    SpedliteScaffold(
        horizontalAlignment = Alignment.CenterHorizontally,
        topBar = {
            SpedliteTopAppBar(actions = {
                DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
            })
        }) {
        var changePasswordExpanded by remember { mutableStateOf(false) }
        val changePasswordRotation by animateFloatAsState(if (changePasswordExpanded) 180f else 0f)
        SpedliteListItem(
            onClick = { changePasswordExpanded = !changePasswordExpanded },
            headlineContent = {
                Text(text = stringResource(R.string.settings_change_password))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpedliteTheme.dimen.gap5),
            leadingContent = {
                SpedliteIcon(R.drawable.ic_lock)
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down), contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = changePasswordRotation
                    }
                )
            }
        )
        Gap2()
        AnimatedVisibility(changePasswordExpanded) {
            SpedliteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpedliteTheme.dimen.gridSize * 5)
                    .padding(bottom = SpedliteTheme.dimen.gridSize * 5)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SpedliteTextFieldPassword(
                        value = oldPassword,
                        onValueChange = onOldPasswordChange,
                        label = { Text(text = stringResource(R.string.settings_current_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Gap3()
                    SpedliteTextFieldPassword(
                        value = newPassword,
                        onValueChange = onNewPasswordChange,
                        label = { Text(text = stringResource(R.string.settings_new_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Gap3()
                    SpedliteTextFieldPassword(
                        value = newPasswordRepeat,
                        onValueChange = onNewPasswordRepeatChange,
                        label = { Text(text = stringResource(R.string.settings_repeat_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    Gap5()
                    SpedliteButton(
                        onClick = onChangePasswordClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = oldPassword.isNotBlank() && newPassword.isNotBlank() && newPasswordRepeat.isNotBlank() && (newPassword == newPasswordRepeat)
                    ) { Text("Save new password") }

                    AnimatedVisibility(passwordChangedSuccessfully) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = SpedliteTheme.dimen.gridSize * 5, bottom = SpedliteTheme.dimen.gridSize * 2)
                        ) {
                            Image(painter = painterResource(R.drawable.ic_success), contentDescription = null)
                            Gap3()
                            Text(text = stringResource(R.string.settings_password_success), style = MaterialTheme.typography.bodyMedium, color = SpedliteTheme.colorScheme.success)
                        }
                    }
                }
            }
        }

        var privacyExpanded by remember { mutableStateOf(false) }
        val privacyRotation by animateFloatAsState(if (privacyExpanded) 180f else 0f)
        SpedliteListItem(
            onClick = { privacyExpanded = !privacyExpanded },
            headlineContent = {
                Text(text = stringResource(R.string.settings_privacy))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpedliteTheme.dimen.gap5),
            leadingContent = {
                SpedliteIcon(R.drawable.ic_shield)
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down), contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = privacyRotation
                    }
                )
            }
        )
        Gap2()
        AnimatedVisibility(privacyExpanded) {
            SpedliteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpedliteTheme.dimen.gridSize * 5)
                    .padding(bottom = SpedliteTheme.dimen.gridSize * 5)
            ) {
                Text(text = AnnotatedString.fromHtml(privacyPolicyHtml), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Light)
            }
        }
        SpedliteListItem(
            onClick = onLogoutClick,
            headlineContent = {
                Text(text = stringResource(R.string.settings_logout), color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpedliteTheme.dimen.gap5),
            leadingContent = {
                SpedliteIcon(R.drawable.ic_logout, contentColor = MaterialTheme.colorScheme.error)
            }
        )
    }
}


@PreviewLightDark
@Composable
private fun Preview() {
    SpedliteTheme {
        SettingsScreen(
            {},
            {},
            { },
            true,
            "",
            {},
            "",
            {},
            "",
            {}
        )
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

    var passwordChangedSuccessfully by mutableStateOf(false)

    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var newPasswordRepeat by mutableStateOf("")

    fun onOldPasswordChange(value: String) {
        oldPassword = value
        passwordChangedSuccessfully = false
    }

    fun onNewPasswordChange(value: String) {
        newPassword = value
        passwordChangedSuccessfully = false
    }

    fun onNewPasswordRepeatChange(value: String) {
        newPasswordRepeat = value
        passwordChangedSuccessfully = false
    }

    fun toggleAppTheme(isLightMode: Boolean) {
        settings.setAppTheme(if (isLightMode) AppTheme.Light else AppTheme.Dark)
    }

    fun onLogoutClick() {
        dialogManager.showConfirmDialog(
            ConfirmDialog(message = R.string.settings_logout_confirm, onConfirm = {
                apiClient.clearToken()
                appNavigator.setRoot(Screen.Login())
            })
        )
    }

    fun onChangePassword() {
        viewModelScope.launch {
            dialogManager.showProgressDialog()
            val result = runCatchingCoroutine { apiClient.changePassword(oldPassword, newPassword, newPasswordRepeat) }
            dialogManager.dismissProgressDialog()
            result.onSuccess {
                passwordChangedSuccessfully = true
                oldPassword = ""
                newPassword = ""
                newPasswordRepeat = ""
            }.onFailure {
                dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
            }
        }
    }

}