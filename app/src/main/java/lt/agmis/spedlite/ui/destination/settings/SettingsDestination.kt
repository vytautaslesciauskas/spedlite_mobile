package lt.agmis.spedlite.ui.destination.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import lt.agmis.spedlite.navigation.ConfirmDialog
import lt.agmis.spedlite.navigation.DialogManager
import lt.agmis.spedlite.navigation.InfoDialog
import lt.agmis.spedlite.navigation.Screen
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.settings.AppTheme
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.ui.component.DarkModeSwitch
import lt.agmis.spedlite.ui.component.Gap2
import lt.agmis.spedlite.ui.component.Gap3
import lt.agmis.spedlite.ui.component.Gap4
import lt.agmis.spedlite.ui.component.Gap5
import lt.agmis.spedlite.ui.component.GapWeight
import lt.agmis.spedlite.ui.component.SpedliteButton
import lt.agmis.spedlite.ui.component.SpedliteCard
import lt.agmis.spedlite.ui.component.SpedliteIcon
import lt.agmis.spedlite.ui.component.SpedliteIconButton
import lt.agmis.spedlite.ui.component.SpedliteListItem
import lt.agmis.spedlite.ui.component.SpedliteScaffold
import lt.agmis.spedlite.ui.component.SpedliteTextFieldPassword
import lt.agmis.spedlite.ui.component.SpedliteTopAppBar
import lt.agmis.spedlite.ui.theme.SpedliteTheme
import lt.agmis.spedlite.usecase.LogoutUseCase
import lt.agmis.spedlite.util.ExceptionMessageParser
import lt.agmis.spedlite.util.runCatchingCoroutine

private val privacyPolicyHtml = "Last updated: 2026.01.19\n" +
        "1. Introduction\n" +
        "JSC Spedlite (\"we\", \"our\", or \"us\") respects your privacy and is committed to protecting your personal data. This Privacy Policy explains how we collect, use, store, and protect personal information when you use our mobile application (the \"App\").\n" +
        "The App is designed for transport companies and their drivers to receive and manage work-related tasks assigned by dispatchers or managers.\n" +
        "By using the App, you agree to the collection and use of information in accordance with this Privacy Policy.\n" +
        "2. Data Controller\n" +
        "The data controller responsible for your personal data is:\n" +
        "JSC Spedlite\n" +
        "Email: info@spedlite.com\n" +
        "Country of registration: Lithuania\n" +
        "3. Information We Collect\n" +
        "We collect only the minimum data necessary for the App to function.\n" +
        "3.1 Personal Data\n" +
        "Username\n" +
        "Password (stored in encrypted form)\n" +
        "3.2 Location Data\n" +
        "The App collects real-time location (GPS) data to enable task-related tracking and operational coordination.\n" +
        "Location data is collected only while the App is in use and according to device permissions.\n" +
        "3.3 Information We Do Not Collect\n" +
        "We do not collect:\n" +
        "Device identifiers (IMEI, device ID)\n" +
        "IP addresses\n" +
        "Browser or operating system details\n" +
        "Photos, documents, signatures\n" +
        "Task history beyond active operational use\n" +
        "Sensitive personal data\n" +
        "4. Purpose of Data Processing\n" +
        "We process personal data for the following purposes:\n" +
        "User authentication and account management\n" +
        "Secure login and password management\n" +
        "Assigning and managing work-related tasks\n" +
        "Location-based operational coordination\n" +
        "Ensuring proper functioning and security of the App\n" +
        "5. Legal Basis for Processing (GDPR)\n" +
        "We process your personal data based on:\n" +
        "Performance of a contract (Article 6(1)(b) GDPR)\n" +
        "Legitimate interests related to service provision and operational management (Article 6(1)(f) GDPR)\n" +
        "User consent for location data collection, which can be withdrawn at any time via device settings\n" +
        "6. Third-Party Services\n" +
        "We use Firebase (provided by Google LLC) as a backend and infrastructure service.\n" +
        "Firebase may process data on our behalf strictly according to our instructions and in compliance with GDPR.\n" +
        "No personal data is sold, shared, or transferred to clients, partners, or other third parties.\n" +
        "For more information, please refer to Google’s Privacy Policy.\n" +
        "7. Data Storage and Security\n" +
        "All personal data is stored securely using industry-standard safeguards.\n" +
        "Passwords are encrypted and cannot be viewed in plain text.\n" +
        "Access to data is limited to authorized personnel only.\n" +
        "8. Data Retention\n" +
        "Personal data is stored only for as long as the user account is active.\n" +
        "When a user account is deleted, all associated personal data is permanently removed.\n" +
        "Data is not retained after termination of employment or service usage.\n" +
        "9. User Rights (GDPR)\n" +
        "As a user, you have the right to:\n" +
        "Access your personal data\n" +
        "Correct inaccurate or incomplete data\n" +
        "Request deletion of your data\n" +
        "Restrict or object to processing\n" +
        "Withdraw consent for location tracking\n" +
        "Data portability, where applicable\n" +
        "To exercise your rights, please contact us at info@spedlite.com\n" +
        "10. Account Deletion\n" +
        "Users can request account deletion directly through the App or by contacting us.\n" +
        "Once deleted, the account and all related personal data will be permanently removed.\n" +
        "11. Children’s Privacy\n" +
        "The App is not intended for children under the age of 16.\n" +
        "We do not knowingly collect personal data from children.\n" +
        "12. Changes to This Privacy Policy\n" +
        "We may update this Privacy Policy from time to time.\n" +
        "Any changes will be communicated through the App or by updating the \"Last updated\" date.\n" +
        "13. Contact Us\n" +
        "If you have any questions or concerns regarding this Privacy Policy or data protection, please contact us: info@spedlite.com"

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
            SpedliteTopAppBar(
                actions = {
                    DarkModeSwitch(modifier = Modifier, onCheckedChange = toggleAppTheme)
                },
                navigationIcon = {
                    val onBack = LocalOnBackPressedDispatcherOwner.current
                    SpedliteIconButton(onClick = {
                        onBack?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
                    }
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
                SelectionContainer {
                    Text(text = privacyPolicyHtml, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Light)
                }
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
        GapWeight()
        Text(
            text = stringResource(R.string.common_version, "${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}"),
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB1B2B4)),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Gap4()
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
    private val apiClient: SpedliteApi,
    private val exceptionMessageParser: ExceptionMessageParser,
    private val logoutUseCase: LogoutUseCase
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
                    appContainer.logoutUseCase,
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
                logout()
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

    private fun logout() {
        viewModelScope.launch {
            dialogManager.showProgressDialog()
            val result = logoutUseCase.logout()
            dialogManager.dismissProgressDialog()
            result.onSuccess {
                appNavigator.setRoot(Screen.Login())
            }
                .onFailure {
                    dialogManager.showInfoDialog(InfoDialog(exceptionMessageParser.parseMessageOrDefault(it)))
                }

        }
    }

}