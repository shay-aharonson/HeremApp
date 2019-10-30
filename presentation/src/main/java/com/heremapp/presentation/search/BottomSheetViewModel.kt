package com.heremapp.presentation.search

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import androidx.databinding.ObservableField
import com.heremapp.presentation.extensions.NonNullObservableField
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.Optional
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for the bottom sheet display. Manages the display state of the view and allows other components to request
 * display changes.
 */
class BottomSheetViewModel @Inject constructor(private val lifecycle: Observable<ActivityEvent>,
                                               private val mainViewModel: MainViewModel,
                                               private val navigator: BottomSheetNavigator) {

    companion object {
        const val DISPLAY_STATE_HIDDEN = 1 // Not visible at all
        const val DISPLAY_STATE_PEEK = 2 // Lip is visible
        const val DISPLAY_STATE_FULL = 3 // Full display

        @Target(AnnotationTarget.EXPRESSION, AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            DISPLAY_STATE_HIDDEN,
            DISPLAY_STATE_PEEK,
            DISPLAY_STATE_FULL
        )
        annotation class DisplayState
    }

    val displayState = ObservableField<@DisplayState Int?>(
        DISPLAY_STATE_HIDDEN
    )

    // Minimal requirement to display the bottom sheet
    private val hasPlacesData = NonNullObservableField(false)

    init {
        displayState.onChanged {
            log("Display state changed: $it")
        }

        setListeners()
    }

    @SuppressLint("CheckResult")
    fun setListeners() {
        // Observe changes to place selection and update the place display.
        mainViewModel.observePlaceSelection()
            .mainThread(lifecycle)
            .subscribeOn(Schedulers.computation())
            .subscribe(::selectedPlaceUpdated, ::log)

        // Observe changes to places discovered and update the places display.
        mainViewModel.observePlaceDiscovery()
            .mainThread(lifecycle)
            .subscribeOn(Schedulers.computation())
            .subscribe(::discoveredPlacesUpdated, ::log)
    }

    /**
     * Notify this viewModel of any UI driven changes to the display state.
     */
    fun updateDisplayState(@DisplayState newState: Int) {
        if (displayState.get() != newState)
            displayState.set(newState)
    }

    /**
     * Is the display currently fully expanded to the user.
     */
    fun isVisible(): Boolean {
        return displayState.get() == DISPLAY_STATE_FULL
    }

    /**
     * A new place has been selected. Update data and display the place view.
     */
    private fun selectedPlaceUpdated(place: PlaceViewModel) {
        displayState.set(DISPLAY_STATE_FULL)
        navigator.showPlace()
    }

    /**
     * A new set of places have been discovered, update data set and hide or display the view accordingly.
     */
    private fun discoveredPlacesUpdated(places: Optional<List<PlaceViewModel>>) {
        log("Places updated: ${places.valueOrNull}")

        if (places.isNull || places.get().isEmpty()) {
            hasPlacesData.set(false)
            displayState.set(DISPLAY_STATE_HIDDEN)
        } else {
            navigator.showPlaces()
            hasPlacesData.set(true)
            displayState.set(DISPLAY_STATE_FULL)
        }
    }

    /**
     * A component has requested to dismiss the bottom sheet, we will only want to set the display state to
     * [DISPLAY_STATE_PEEK] if it is currently being displayed. Otherwise such an action could enable an empty sheet.
     * @return a boolean value whether or not the request has been addressed.
     */
    fun requestDismissBottomSheet(): Boolean {
        return if (displayState.get() == DISPLAY_STATE_FULL) {
            displayState.set(DISPLAY_STATE_PEEK)
            true
        } else
            false
    }

    /**
     * A component has requested to display the bottom sheet, we will only want to set the display state to
     * [DISPLAY_STATE_FULL] if any places have been discovered. Otherwise such an action could enable an empty sheet.
     * @return a boolean value whether or not the request has been addressed.
     */
    fun requestDisplayBottomSheet(): Boolean {
        return if (hasPlacesData.get()) {
            displayState.set(DISPLAY_STATE_FULL)
            true
        } else
            false
    }
}