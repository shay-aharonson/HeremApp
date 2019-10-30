package com.heremapp.presentation.permissions

import com.heremapp.communication.NetworkSupervisorService
import com.heremapp.presentation.extensions.NonNullObservableField
import com.location.LocationService

/**
 * View model that handles the logic for checking and requesting location permissions.
 */
class RequestPermissionsViewModel(private val permissionsProvider: PermissionsProvider,
                                  private val locationService: LocationService,
                                  private val networkSupervisorService: NetworkSupervisorService) : RequestPermissionCallback {
    companion object {
        const val MAX_REQUESTS = 2
    }

    // TODO: USE
    val hasPermissions = NonNullObservableField(false)
    // Monitor the number of permission requests.
    private var requestCount = 0

    init {
        permissionsProvider.arePermissionsGranted().let { hasPermissions ->
            this.hasPermissions.set(hasPermissions)

            if (!hasPermissions) {
                requestPermissions()
            }
        }
    }

    /**
     * Permissions granted, notify relevant services.
     */
    override fun onPermissionGranted(resultCode: Int?) {
        hasPermissions.set(true)
        locationService.onPermissionsGranted()
        networkSupervisorService.onPermissionsGranted()
    }

    /**
     * One or more permissions were denied, request permissions for the failed requests if possible.
     */
    override fun onPermissionDenied(grantResults: IntArray, resultCode: Int?) {
        requestCount++
        hasPermissions.set(false)

        if (requestCount < MAX_REQUESTS)
           requestPermissions(grantResults)
    }

    /**
     * Request all permissions or only those that failed previous requests, by a given grantResults array.
     */
    fun requestPermissions(grantResults: IntArray? = null) {
        permissionsProvider.requestPermissions(grantResults)
    }
}