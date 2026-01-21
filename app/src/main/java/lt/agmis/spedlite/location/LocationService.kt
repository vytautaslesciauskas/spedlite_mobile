package lt.agmis.spedlite.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import lt.agmis.spedlite.App
import lt.agmis.spedlite.AppScope
import lt.agmis.spedlite.R
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.settings.SpedliteSettings
import lt.agmis.spedlite.util.runCatchingCoroutine
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var apiClient: SpedliteApi
    private lateinit var settings: SpedliteSettings
    private var isTracking = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val NOTIFICATION_ID = 1234
        private const val CHANNEL_ID = "location_service_channel"

        fun start(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Napier.d("Location service created")
        val appContainer = (application as App).appContainer
        apiClient = appContainer.apiClient
        settings = appContainer.settings
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Napier.d("Location service started")
        if (!hasPermissionForLocationService(application)) {
            Napier.e("Location permissions not granted. Stopping service.")
            stopSelf()
            return START_NOT_STICKY
        }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        if (!isTracking) {
            requestLocationUpdates()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        scope.launch {
            isTracking = true
            while (isActive) {
                try {
                    val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                    updateLocation(location.latitude, location.longitude)
                    delay(settings.getRefresh().toDuration(DurationUnit.SECONDS))
                } catch (unlikely: SecurityException) {
                    Napier.e("Lost location permission. Could not request updates.", unlikely)
                    break
                } catch (exception: Exception) {
                    Napier.e("Failed to get location", exception)
                }
            }
            isTracking = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(lat: Double, lng: Double) {
        AppScope.launch {
            val result = runCatchingCoroutine {
                val networkType = getNetworkType(applicationContext)
                apiClient.updateLocation(lat, lng, networkType.source)
            }
            result.onSuccess {
                Napier.d("Location updated: $lat, $lng")
            }
            result.onFailure {
                Napier.e("Failed to update location", it)
            }
        }
    }


    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_truck) // Reusing existing icon
            .setContentTitle(getString(R.string.location_service_notification_title))
            .setContentText(getString(R.string.location_service_notification_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Napier.d("Location service destroyed")
        isTracking = false
        scope.coroutineContext.cancelChildren()
    }
}

enum class NetworkType(val source: String) {
    WIFI("wifi"),
    CELLULAR_2G("2g"),
    CELLULAR_3G("3g"),
    CELLULAR_4G("4g"),
    CELLULAR_5G("5g"),
    ETHERNET("ethernet"),
    NONE("none")
}

@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun getNetworkType(context: Context): NetworkType {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getCellularGeneration(context)
        else -> NetworkType.NONE
    }
}

@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
private fun getCellularGeneration(context: Context): NetworkType {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    return when (telephonyManager.dataNetworkType) {
        TelephonyManager.NETWORK_TYPE_NR -> NetworkType.CELLULAR_5G

        TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.CELLULAR_4G

        TelephonyManager.NETWORK_TYPE_UMTS,
        TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_EVDO_A,
        TelephonyManager.NETWORK_TYPE_EVDO_B,
        TelephonyManager.NETWORK_TYPE_HSDPA,
        TelephonyManager.NETWORK_TYPE_HSUPA,
        TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_HSPAP,
        TelephonyManager.NETWORK_TYPE_EHRPD -> NetworkType.CELLULAR_3G

        TelephonyManager.NETWORK_TYPE_GPRS,
        TelephonyManager.NETWORK_TYPE_EDGE,
        TelephonyManager.NETWORK_TYPE_CDMA,
        TelephonyManager.NETWORK_TYPE_1xRTT,
        TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.CELLULAR_2G

        else -> NetworkType.CELLULAR_4G // Default fallback for unknown cellular
    }
}


private fun hasPermissionForLocationService(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
}
