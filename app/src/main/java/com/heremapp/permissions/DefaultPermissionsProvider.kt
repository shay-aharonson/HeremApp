package com.heremapp.permissions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.heremapp.presentation.permissions.PermissionsProvider
import com.heremapp.ui.base.BaseActivity
import com.heremapp.utility.messaging.MessageHandler.Companion.log

/**
 * Provider of location and network state permissions.
 * TODO: Update the permissions logic to separate between location and network, arePermissionsGranted().
 * TODO: Also pretty sure data network does not require runtime user permission, just manifest declaration.
 */
class DefaultPermissionsProvider(private val activity: BaseActivity): PermissionsProvider {

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 200
        const val LOCATION_RESULT_INDEX = 0
        const val NETWORK_STATE_RESULT_INDEX = 1


        val PERMISSIONS_INDEX_MAP: Map<Int, String> = mapOf(
            LOCATION_RESULT_INDEX to ACCESS_FINE_LOCATION,
            NETWORK_STATE_RESULT_INDEX to ACCESS_NETWORK_STATE)
    }

    /**
     * Request all necessary permissions or if specified by the given grantResults, only those that failed a previous
     * request.
     */
    override fun requestPermissions(grantResults: IntArray?) {
        log("Requesting permissions..")

        ActivityCompat.requestPermissions(
            activity,
            getFailedPermissionArray(grantResults) ?: arrayOf( ACCESS_FINE_LOCATION, ACCESS_NETWORK_STATE),
            REQUEST_CODE_PERMISSIONS
        )
    }

    /**
     * Check all necessary permissions are granted.
     */
    override fun arePermissionsGranted(): Boolean {
        return hasLocationPermissions() && hasNetworkStatePermissions()
    }

    /**
     * Check the location permission is granted.
     */
    private fun hasLocationPermissions(): Boolean {
        return (checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED).also {
            log("Location permissions granted- [$this]")
        }
    }

    /**
     * Check the network state permission is granted.
     */
    private fun hasNetworkStatePermissions(): Boolean {
        return (checkSelfPermission(activity, ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED).also {
            log("Network state permissions granted- [$this]")
        }
    }

    /**
     * Maps any failed grantResults to their appropriate permission string and returns an array of all failed
     * permissions.
     * TODO: This logic is incorrect, can result in wrong codes being sent. Need to separate the requests with different request codes and handle each accordingly.
     */
    private fun getFailedPermissionArray(grantResults: IntArray?): Array<out String>? {
        val result = ArrayList<String>()

        grantResults?.forEachIndexed { index: Int, value: Int ->
            if (value == PERMISSION_DENIED)
                PERMISSIONS_INDEX_MAP[index]?.let(result::add) ?: throw IllegalArgumentException("Unknown Permission")
        }

        return if (result.isEmpty()) null else result.toTypedArray()
    }
}