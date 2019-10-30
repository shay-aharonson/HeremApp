package com.heremapp.communication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that monitors the active network connection. Allows subscribers to be notified when the connectivity drops or
 * connects.
 */
@Singleton
class NetworkSupervisorService @Inject constructor(private val context: Context) {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLosing(network: Network, maxMsToLive: Int) {
            log("Network losing!")
            super.onLosing(network, maxMsToLive)
        }

        override fun onLost(network: Network) {
            log("Network lost!")
            hasNetworkSubject.onNext(false)
            super.onLost(network)
        }

        override fun onUnavailable() {
            log("Network unavailable!")
            super.onUnavailable()
        }

        override fun onAvailable(network: Network) {
            log("Network available!")
            hasNetworkSubject.onNext(true)
            super.onAvailable(network)
        }
    }

    private val hasNetworkSubject = BehaviorSubject.createDefault(false)

    init {
        // Check for permissions and track location when granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)
            registerToNetworkChanges()
    }

    fun observeNetworkChanges(): Observable<Boolean> {
        return hasNetworkSubject
    }

    fun hasNetwork(): Boolean {
        return hasNetworkSubject.value ?: false
    }

    /**
     * Network data permission granted.
     */
    fun onPermissionsGranted() {
        registerToNetworkChanges()
    }

    /**
     * Set the current network state and register to changes.
     */
    private fun registerToNetworkChanges() {
        log("Registering to network changes..")
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        hasNetworkSubject.onNext(cm.activeNetwork != null)
        cm.registerNetworkCallback(NetworkRequest.Builder().apply {
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
        }.build(), networkCallback)
    }
}