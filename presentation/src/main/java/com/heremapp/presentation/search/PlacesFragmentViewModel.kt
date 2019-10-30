package com.heremapp.presentation.search

import android.annotation.SuppressLint
import androidx.databinding.ObservableField
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.Optional
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for the discovered places display. Observes changes to discovered places data.
 */
class PlacesFragmentViewModel(private val mainViewModel: MainViewModel,
                              private val lifecycle: Observable<FragmentEvent>) {

    val places = ObservableField<List<PlaceViewModel>>()

    init {
        setDiscoveredPlacesListener()
    }

    /**
     * Notifies this viewModel that the user a selected a place from the list of discovered places.
     */
    fun onPlaceClicked(place: PlaceViewModel) {
        mainViewModel.onPlaceSelected(place)
    }

    @SuppressLint("CheckResult")
    private fun setDiscoveredPlacesListener() {
        mainViewModel.observePlaceDiscovery()
            .mainThread(lifecycle)
            .subscribeOn(Schedulers.computation())
            .subscribe(::discoveredPlacesUpdated, ::log)
    }

    private fun discoveredPlacesUpdated(places: Optional<List<PlaceViewModel>>) {
        log("Places updated: ${places.valueOrNull}")
        this.places.set(places.valueOrNull)
    }
}