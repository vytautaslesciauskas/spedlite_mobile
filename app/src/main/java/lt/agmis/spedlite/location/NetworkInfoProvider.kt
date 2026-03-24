@file:Suppress("DEPRECATION")

package lt.agmis.spedlite.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.telephony.PhoneStateListener
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

data class NetworkInfo(
    val networkType: Int,
    val overrideNetworkType: Int
)

class NetworkInfoProvider(private val appContext: Context) {

    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager =
        appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Volatile
    private var currentTransport = resolveTransport(
        connectivityManager.activeNetwork?.let(connectivityManager::getNetworkCapabilities)
    )

    @Volatile
    private var latestCellularInfo = NetworkInfo(
        networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN,
        overrideNetworkType = TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE
    )

    @Volatile
    private var isNetworkCallbackRegistered = false

    @Volatile
    private var isTelephonyListenerRegistered = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshActiveTransport()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            if (connectivityManager.activeNetwork == network) {
                currentTransport = resolveTransport(networkCapabilities)
            }
        }

        override fun onLost(network: Network) {
            refreshActiveTransport()
        }
    }

    private val phoneStateListener = object : PhoneStateListener() {
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
            latestCellularInfo = NetworkInfo(
                networkType = telephonyDisplayInfo.networkType,
                overrideNetworkType = telephonyDisplayInfo.overrideNetworkType
            )
        }
    }

    @Synchronized
    @SuppressLint("MissingPermission")
    fun startListening() {
        refreshActiveTransport()
        if (!isNetworkCallbackRegistered) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            isNetworkCallbackRegistered = true
        }

        if (hasReadPhoneStatePermission() && !isTelephonyListenerRegistered) {
            latestCellularInfo =
                latestCellularInfo.copy(networkType = telephonyManager.dataNetworkType)
            telephonyManager.listen(
                phoneStateListener,
                PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED
            )
            isTelephonyListenerRegistered = true
        }
    }

    @Synchronized
    fun stopListening() {
        if (!isNetworkCallbackRegistered && !isTelephonyListenerRegistered) {
            return
        }

        if (isNetworkCallbackRegistered) {
            runCatching {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
            isNetworkCallbackRegistered = false
        }

        if (isTelephonyListenerRegistered) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
            isTelephonyListenerRegistered = false
        }
    }

    fun getLatestNetworkInfo(): NetworkInfo {
        return when (currentTransport) {
            NetworkTransport.WIFI -> NetworkInfo(
                SOURCE_WIFI,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE
            )

            NetworkTransport.ETHERNET -> NetworkInfo(
                SOURCE_ETHERNET,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE
            )

            NetworkTransport.CELLULAR -> latestCellularInfo
            NetworkTransport.NONE,
            NetworkTransport.OTHER -> NetworkInfo(
                TelephonyManager.NETWORK_TYPE_UNKNOWN,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE
            )
        }
    }

    private fun refreshActiveTransport() {
        currentTransport = resolveTransport(
            connectivityManager.activeNetwork?.let(connectivityManager::getNetworkCapabilities)
        )
    }

    private fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun resolveTransport(capabilities: NetworkCapabilities?): NetworkTransport {
        return when {
            capabilities == null -> NetworkTransport.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkTransport.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkTransport.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkTransport.CELLULAR
            else -> NetworkTransport.OTHER
        }
    }

    private enum class NetworkTransport {
        NONE,
        WIFI,
        ETHERNET,
        CELLULAR,
        OTHER
    }

    companion object {
        const val SOURCE_WIFI = -1
        const val SOURCE_ETHERNET = -2
    }
}
