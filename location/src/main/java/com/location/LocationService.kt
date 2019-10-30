package com.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.Optional
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that actively tracks users location given location permissions are granted.
 */
@Singleton
class LocationService @Inject constructor(private val context: Context) {

    private val lastKnownLocationSubject = BehaviorSubject.create<Optional<Location>>()

    init {
        // Check for permissions and track location when granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            trackLocation()
    }

    /**
     * Called when location permissions are granted, in order to start tracking users location.
     */
    fun onPermissionsGranted() {
        trackLocation()
    }

    fun getLocation(): Location? {
        return lastKnownLocationSubject.value?.valueOrNull
    }

    fun hasLocation(): Boolean {
        return lastKnownLocationSubject.value?.valueOrNull != null
    }

    fun observeLocationChanges(): Observable<Optional<Location>> {
        return lastKnownLocationSubject
    }

    @SuppressLint("MissingPermission")
    private fun trackLocation() {
        log("Permissions granted tracking location...")
        LocationServices.getFusedLocationProviderClient(context).apply {

            lastLocation.addOnSuccessListener { location: Location? ->
                log( "Initial location value - [$location]")
                lastKnownLocationSubject.onNext(Optional(location))
            }

            requestLocationUpdates(
                LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                object: LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        log("Location update - [$locationResult]")

                        // Make sure we update values only if locations differ
                        if (lastKnownLocationSubject.value?.valueOrNull == null ||
                            lastKnownLocationSubject.value?.valueOrNull?.distanceTo(locationResult.lastLocation) != 0f)
                            lastKnownLocationSubject.onNext(Optional(locationResult.lastLocation))
                    }
                }, null)
        }
    }
}