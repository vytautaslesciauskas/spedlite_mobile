package lt.agmis.spedlite.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import lt.agmis.spedlite.R

class DialogManager {

    var infoDialog by mutableStateOf<InfoDialog?>(null)
    var confirmDialog by mutableStateOf<ConfirmDialog?>(null)
    var progressDialog by mutableStateOf(false)

    fun showInfoDialog(infoDialog: InfoDialog) {
        this.infoDialog = infoDialog
    }

    fun dismissInfoDialog() {
        infoDialog = null
    }

    fun showProgressDialog() {
        progressDialog = true
    }

    fun dismissProgressDialog() {
        progressDialog = false
    }

    fun showConfirmDialog(confirmDialog: ConfirmDialog) {
        this.confirmDialog = confirmDialog
    }

    fun dismissConfirmDialog() {
        confirmDialog = null
    }

}


data class InfoDialog(val message: UIText) {
    constructor(message: String) : this(UIText.RawString(message))
    constructor(message: Int) : this(UIText.Resource(message, arrayOf()))
}

data class ConfirmDialog(
    val message: UIText,
    val positiveButtonText: UIText = UIText.Resource(R.string.common_yes, arrayOf()),
    val onConfirm: () -> Unit,
    val cancelButtonText: UIText = UIText.Resource(R.string.common_cancel, arrayOf()),
    val onCancel: (() -> Unit)? = null
) {
    constructor(message: String, positiveButtonText: String, onConfirm: () -> Unit, onCancel: (() -> Unit)?) : this(
        message = UIText.RawString(message),
        positiveButtonText = UIText.RawString(positiveButtonText),
        onConfirm = onConfirm,
        onCancel = onCancel
    )

    constructor(message: Int, positiveButtonText: Int = R.string.common_yes, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) : this(
        message = UIText.Resource(message, arrayOf()),
        positiveButtonText = UIText.Resource(positiveButtonText, arrayOf()),
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

data class WarningDialog(
    val title: UIText,
    val message: UIText,
    val confirmButtonText: UIText,
    val onConfirm: () -> Unit,
    val onCancel: (() -> Unit)? = null
) {
    constructor(title: String, message: String, confirmButtonText: String, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) : this(
        title = UIText.RawString(title),
        message = UIText.RawString(message),
        confirmButtonText = UIText.RawString(confirmButtonText),
        onConfirm = onConfirm,
        onCancel = onCancel
    )

    constructor(title: String, message: String, confirmButtonText: Int, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) : this(
        title = UIText.RawString(title),
        message = UIText.RawString(message),
        confirmButtonText = UIText.Resource(confirmButtonText, arrayOf()),
        onConfirm = onConfirm,
        onCancel = onCancel
    )

    constructor(title: Int, message: Int, confirmButtonText: Int, onConfirm: () -> Unit, onCancel: (() -> Unit)? = null) : this(
        title = UIText.Resource(title, arrayOf()),
        message = UIText.Resource(message, arrayOf()),
        confirmButtonText = UIText.Resource(confirmButtonText, arrayOf()),
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

sealed interface UIText {
    class Resource(val resId: Int, val arguments: Array<Any>) : UIText

    class RawString(val value: String) : UIText

    companion object {
        fun of(value: String): UIText {
            return RawString(value)
        }

        fun of(@StringRes resId: Int): UIText {
            return Resource(resId, arrayOf())
        }

        fun of(@StringRes resId: Int, arguments: Array<Any>): UIText {
            return Resource(resId, arguments)
        }
    }
}


fun String.toUIText(): UIText = UIText.RawString(this)
fun Int.toUIText(): UIText = UIText.Resource(this, arrayOf())

fun Int.toUIText(vararg arguments: Any): UIText = UIText.Resource(this, arrayOf(*arguments))