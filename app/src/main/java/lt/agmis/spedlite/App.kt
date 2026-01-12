package lt.agmis.spedlite

import android.app.Application
import io.github.aakira.napier.DebugAntilog
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
        setupLogging()
    }


    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }
}

object AppScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()
}

