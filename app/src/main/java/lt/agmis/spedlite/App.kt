package lt.agmis.spedlite

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import lt.agmis.spedlite.di.AppContainer
import kotlin.coroutines.CoroutineContext

class App : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        appContainer.networkInfoProvider.startListening()
        setupLogging()
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = false
            Napier.base(DebugAntilog())
        } else {
            Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)
            Napier.base(CrashlyticsAntilog())
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        }
    }
}

object AppScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()
}


class CrashlyticsAntilog : io.github.aakira.napier.Antilog() {
    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        if (throwable != null && message != null) {
            FirebaseCrashlytics.getInstance().log(message)
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } else {
            if (priority >= LogLevel.DEBUG && message != null) {
                FirebaseCrashlytics.getInstance().log(message)
            }
        }
    }
}
