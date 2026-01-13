package lt.agmis.spedlite.util

import android.content.Context
import lt.agmis.spedlite.R
import lt.agmis.spedlite.network.BackendException

class ExceptionMessageParser(
    private val context: Context
) {

    fun parseMessageOrNull(exception: Throwable): String? {
        return when (exception) {
            is BackendException -> exception.body?.error?.takeUnless { it.isBlank() }
                ?: exception.message?.takeUnless { it.isBlank() }
            else -> null
        }
    }

    fun parseMessageOrDefault(exception: Throwable, defaultRes: Int = R.string.error_default): String {
        return parseMessageOrNull(exception) ?: context.getString(defaultRes)
    }
}