package com.heremapp.presentation.search

import android.annotation.SuppressLint
import androidx.databinding.ObservableField
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for the selected place display. Observes changes to selected place data.
 */
class PlaceFragmentViewModel @Inject constructor(private val mainViewModel: MainViewModel,
                                                 private val lifecycle: Observable<FragmentEvent>) {
    val place = ObservableField<PlaceViewModel>()

    init {
        setSelectedPlaceListener()
    }

    /**
     * Notify this viewModel that the user has changed the personal notes for the selected place.
     */
    fun onNotesEdited(text: String) {
        place.get()?.notes?.set(text)
    }

    @SuppressLint("CheckResult")
    private fun setSelectedPlaceListener() {
        // Observe changes to selected place
        mainViewModel.observePlaceSelection()
            .mainThread(lifecycle)
            .subscribeOn(Schedulers.computation())
            .subscribe(::selectedPlaceUpdated, ::log)
    }

    // TODO: Make nullable?
    private fun selectedPlaceUpdated(place: PlaceViewModel) {
        log("Place updated: $place")
        this.place.set(place)
    }
}