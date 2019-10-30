package com.heremapp.presentation.communication

import android.annotation.SuppressLint
import android.location.Location
import androidx.annotation.IntDef
import com.heremapp.communication.NetworkSupervisorService
import com.heremapp.presentation.base.LoadingViewModel
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.CommunicationStatus
import com.heremapp.presentation.extensions.NonNullObservableField
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.Optional
import com.location.LocationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel that manages the different communication routes, location and network. Provides a [CommunicationStatus]
 * to subscribers based on the current state.
 */
@Singleton
class CommunicationSupervisorViewModel @Inject constructor(
    private val locationService: LocationService,
    private val networkSupervisorService: NetworkSupervisorService) : LoadingViewModel() {

    companion object {
        const val STATUS_ALL = 1
        const val STATUS_NO_INTERNET = 2
        const val STATUS_NO_LOCATION = 3
        const val STATUS_NONE = 4

        @IntDef(
            STATUS_ALL,
            STATUS_NO_INTERNET,
            STATUS_NO_LOCATION,
            STATUS_NONE
        )
        annotation class CommunicationStatus
    }

    // Holds current communication status.
    val communicationStatus = NonNullObservableField(STATUS_NONE)

    init {
        subscribeToLocationChanges()
        subscribeToNetworkChanges()
        setCommunicationState(locationService.hasLocation(), networkSupervisorService.hasNetwork())
    }

    @SuppressLint("CheckResult")
    private fun subscribeToNetworkChanges() {
        networkSupervisorService.observeNetworkChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onNetworkChanged, ::log)
    }

    @SuppressLint("CheckResult")
    private fun subscribeToLocationChanges() {
        locationService.observeLocationChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onLocationChanged, ::log)
    }

    /**
     * Sets the new communication state based on the given new location state and the existing network state.
     */
    private fun onLocationChanged(newLocation: Optional<Location>) {
        log("Location Status: $newLocation")
        setCommunicationState(!newLocation.isNull, networkSupervisorService.hasNetwork())
    }

    /**
     * Sets the new communication state based on the given new network state and the existing location state.
     */
    private fun onNetworkChanged(hasNetwork: Boolean) {
        log("Network Status: $hasNetwork")
        setCommunicationState(locationService.hasLocation(), hasNetwork)
    }

    /**
     * Set the communication state based on the given location and network states.
     */
    private fun setCommunicationState(hasLocation: Boolean, hasNetwork: Boolean) {
        when {
            !hasLocation && !hasNetwork -> STATUS_NONE
            hasLocation && hasNetwork -> STATUS_ALL
            !hasLocation -> STATUS_NO_LOCATION
            !hasNetwork -> STATUS_NO_INTERNET
            else -> throw IllegalStateException("Unknown Communication State!")
        }.let { newState ->
            log("Communication Status: $newState")
            communicationStatus.set(newState)
            isLoading.set(newState != STATUS_ALL)
        }
    }
}