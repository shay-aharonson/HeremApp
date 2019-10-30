package com.heremapp.presentation.main

import com.heremapp.presentation.base.LoadingViewModel
import com.heremapp.presentation.map.MapFragmentViewModel.Companion.DEFAULT_SEARCH_RADIUS
import com.heremapp.utility.rx.Optional
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Used to bridge between the tasked ViewModels within the [MainActivity].
 */
class MainViewModel : LoadingViewModel() {

    private val selectedCategories = BehaviorSubject.createDefault<Optional<List<CategoryViewModel>>>(Optional(null))
    private val discoveredPlaces = BehaviorSubject.createDefault<Optional<List<PlaceViewModel>>>(Optional(null))
    private val selectedPlace = BehaviorSubject.create<PlaceViewModel>()
    private val persistedPlaces = BehaviorSubject.createDefault<List<PlaceViewModel>>(emptyList())
    private val searchRadius = BehaviorSubject.createDefault(DEFAULT_SEARCH_RADIUS)

    /**
     * Notify the [MainViewModel] that new categories have been selected.
     */
    fun onCategoriesSelected(categories: List<CategoryViewModel>?) {
        selectedCategories.onNext(Optional(categories))
    }

    /**
     * Listen to ongoing changes in category selection.
     */
    fun observeCategorySelection(): Observable<Optional<List<CategoryViewModel>>> {
        return selectedCategories
    }

    /**
     * Get the currently selected categories.
     */
    fun getSelectedCategories(): List<CategoryViewModel>? {
        return selectedCategories.value?.valueOrNull
    }

    /**
     * Notify the [MainViewModel] that new places have been discovered.
     */
    fun onPlacesDiscovered(places: List<PlaceViewModel>?) {
        discoveredPlaces.onNext(Optional(places))
    }

    /**
     * Listen to ongoing changes in discovered places.
     */
    fun observePlaceDiscovery(): Observable<Optional<List<PlaceViewModel>>> {
        return discoveredPlaces
    }

    /**
     * Get the currently discovered places.
     */
    fun getPlacesDiscovered(): List<PlaceViewModel>? {
        return discoveredPlaces.value?.valueOrNull
    }

    /**
     * Notify the [MainViewModel] that a new places has been selected.
     */
    fun onPlaceSelected(place: PlaceViewModel) {
        selectedPlace.onNext(place)
    }

    /**
     * Listen to ongoing changes in place selection.
     */
    fun observePlaceSelection(): Observable<PlaceViewModel> {
        return selectedPlace
    }

    /**
     * Get the currently selected place.
     */
    fun getSelectedPlace(): PlaceViewModel? {
        return selectedPlace.value
    }

    /**
     * Notify the [MainViewModel] of an updated list of persisted places.
     */
    fun onPersistedPlacesChanged(places: List<PlaceViewModel>) {
        persistedPlaces.onNext(places)
    }

    /**
     * Listen to ongoing changes in persisted places.
     */
    fun observePersistedPlaces(): Observable<List<PlaceViewModel>> {
        return persistedPlaces
    }

    /**
     * Get the currently persisted places.
     */
    fun getPersistedPlaces(): List<PlaceViewModel>? {
        return persistedPlaces.value
    }

    /**
     * Notify the [MainViewModel] that a new search radius has been set.
     */
    fun onSearchRadiusChanged(radius: Double) {
        searchRadius.onNext(radius)
    }

    /**
     * Listen to ongoing changes in search radius.
     */
    fun observeSearchRadius(): Observable<Double> {
        return searchRadius
    }

    /**
     * Get the current search radius.
     */
    fun getSearchRadius(): Double? {
        return searchRadius.value
    }
}