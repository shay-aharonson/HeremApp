package com.heremapp.presentation.favorites

import android.annotation.SuppressLint
import androidx.databinding.ObservableField
import com.heremapp.presentation.main.MainNavigator
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for the favorites display.
 * Listens to changes in the persisted places and provides data to the display, also reacts to clicked favorites.
 */
class FavoritesFragmentViewModel(private val mainViewModel: MainViewModel,
                                 private val mainNavigator: MainNavigator,
                                 private val lifecycle: Observable<FragmentEvent>) {

    // List of viewModels for the persisted places.
    val places = ObservableField<List<PlaceViewModel>>()

    init {
        setPersistedPlacesListener()
    }

    /**
     * Notify the [MainViewModel] of the change to the current selected place and request to navigate to the map.
     */
    fun onPlaceClicked(place: PlaceViewModel) {
        mainNavigator.requestScreenNavigation(MainNavigator.SCREEN_TYPE_MAP)
        mainViewModel.onPlaceSelected(place)
    }

    /**
     * Subscribe to changes in the persisted places (favorites).
     */
    @SuppressLint("CheckResult")
    private fun setPersistedPlacesListener() {
        mainViewModel.observePersistedPlaces()
            .mainThread(lifecycle)
            .subscribeOn(Schedulers.computation())
            .subscribe(places::set, ::log)
    }
}