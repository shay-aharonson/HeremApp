package com.heremapp.presentation.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.databinding.ObservableField
import com.heremapp.communication.geo.HereGeoService
import com.heremapp.communication.places.HerePlacesService
import com.heremapp.communication.places.PlacesDataSource
import com.heremapp.data.PersistedDataStore
import com.heremapp.data.models.GeoProximityType
import com.heremapp.data.models.place.Address
import com.heremapp.data.models.place.Place
import com.heremapp.presentation.extensions.NonNullObservableField
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.CategoryViewModel
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.messaging.MessageHandler.Companion.logW
import com.heremapp.utility.rx.Optional
import com.heremapp.utility.rx.mainThread
import com.location.LocationService
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Handles logic for the MapFragment, updates the map when new location coordinates are retrieved as well as user
 * selected categories, for which corresponding [Place]'s will be retrieved and displayed to the user.
 */
class MapFragmentViewModel(
    private val hereGeoService: HereGeoService,
    private val locationService: LocationService,
    private val mainViewModel: MainViewModel,
    private val herePlacesService: HerePlacesService,
    private val dataStore: PersistedDataStore,
    private val lifecycle: Observable<FragmentEvent>) {

    companion object {
        // Map search radius constants
        const val DEFAULT_SEARCH_RADIUS = 2000.0
        const val MIN_SEARCH_RADIUS = 500.0
        const val MAX_SEARCH_RADIUS = 50000.0
        const val MIN_SCALE_FACTOR = 0.98f
        const val MAX_SCALE_FACTOR = 1.02f
        const val SEARCH_TIME_INTERVAL_SECONDS = 2L // Radius updated many times per scale, set an interval for searching.
    }

    // Last location reported by the location service.
    val lastKnownLocation = ObservableField<Location>()
    // Last geo data corresponding to lastKnownLocation
    val lastKnownAddress = ObservableField<Address>()
    // List of viewModels for places returned by API for selected categories and search radius.
    val discoveredPlaces = ObservableField<List<PlaceViewModel>>()
    // Place selected by user.
    val selectedPlace = ObservableField<PlaceViewModel>()
    val isSearchModeEnabled = NonNullObservableField(false)
    val searchRadius = NonNullObservableField(DEFAULT_SEARCH_RADIUS)

    private var lastSelectedCategories: List<CategoryViewModel>? = null

    private val persistedPlaces = NonNullObservableField<List<PlaceViewModel>>(emptyList())
    private val placesDataSource = PlacesDataSource(lifecycle, herePlacesService, dataStore)
    // Hot observable to monitor all search radius changes and emit an event per defined interval.
    private val searchRadiusChangeSubject = PublishSubject.create<Double>()

    init {
        setMainViewModelListeners()
        setKnownLocationListener()
        setCategorySelectionListener()
        setPlaceSelectionListener()
        setDataStorePlacesListener()
        setSearchRadiusChangedListener()
    }

    /**
     * User clicked to enable search mode.
     */
    fun onSearchModeClicked() {
        isSearchModeEnabled.set(true)
    }

    /**
     * User clicked on an empty map coordinate.
     */
    fun onEmptyMapLocationClicked() {
        log("Map clicked, clearing selected place.")
        selectedPlace.set(null)
        isSearchModeEnabled.set(false)
    }

    /**
     * User performed a scale gesture with the given factor.
     */
    fun onScaleGestureDetected(scaleFactor: Float) {
        // Calculate the scaled radius while it is multiplied by a scale factor of: MIN_SCALE_FACTOR <= X <= MAX_SCALE_FACTOR.
        val scaledRadius = searchRadius.get() * Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR))
        log("Scaled radius: $scaledRadius")
        // Calculate a valid new radius of: MIN_SEARCH_RADIUS <= scaledRadius <= MAX_SEARCH_RADIUS
        val validRadius = Math.max(MIN_SEARCH_RADIUS, Math.min(scaledRadius, MAX_SEARCH_RADIUS))
        searchRadius.set(validRadius)
    }

    /**
     * User has selected a place marker on the map.
     */
    fun onPlaceMarkerClicked(placeId: String) {
        isSearchModeEnabled.set(false)
        discoveredPlaces.get()?.firstOrNull { it.id.get() == placeId }
            ?.let {
                selectedPlace.set(it)
            }
    }

    /**
     * Subscribe to changes in the search radius and fetches an updated list of places in the given radius.
     */
    @SuppressLint("CheckResult")
    private fun setSearchRadiusChangedListener() {
        searchRadiusChangeSubject
            .subscribeOn(Schedulers.io())
            .mainThread(lifecycle)
            .throttleLast(SEARCH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS) // Take the last radius emitted every [SEARCH_TIME_INTERVAL_SECONDS]
            .toFlowable(BackpressureStrategy.LATEST)
            .switchMap(::getPlacesForNewSearchRadius)
            .filter { optionalPlaces -> !optionalPlaces.isNull } // Filter "null" data
            .map { it.get() }
            .subscribe(::handlePlacesResponse, ::log)

        // Spam listener with all the radius changes per gesture.
        searchRadius.onChanged(searchRadiusChangeSubject::onNext)
    }

    /**
     * Requests a new list of places from the API given an updated radius and current location, if no location is
     * available or no categories are selected this method will return a boxed null object and log a warning.
     * These are not erroneous states hence no need for exceptions to be thrown.
     */
    private fun getPlacesForNewSearchRadius(radius: Double): Flowable<Optional<List<Place>>> {
        lastKnownLocation.get()?.let { location ->
            lastSelectedCategories?.let { categories ->
                if (categories.isNotEmpty())
                    return placesDataSource.loadPlaces(
                        GeoProximityType(location.latitude, location.longitude, radius.roundToInt()),
                        categories.map { it.category })
                        .toFlowable(BackpressureStrategy.LATEST)
                        .map { Optional(it) }
            }
            logW("No categories selected!")
            return Flowable.just(Optional<List<Place>>(null))
        }
        logW("No location data!")
        return Flowable.just(Optional<List<Place>>(null))
    }

    /**
     * Uses the updated categories selected by the user to retrieve information of the associated [Place]'s.
     */
    private fun selectedCategoriesUpdated(categories: Optional<List<CategoryViewModel>>) {
        log("selected categories: [${categories.valueOrNull?.joinToString { it.category.toString() }}]")
        lastSelectedCategories = categories.valueOrNull

        // Discover places only if categories are selected
        lastKnownLocation.get()?.let { location ->
            if (!categories.isNull && categories.get().isNotEmpty())
                placesDataSource.loadPlaces(
                    GeoProximityType(location.latitude, location.longitude, searchRadius.get().roundToInt()),
                    categories.get().map { it.category }
                )
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribeOn(Schedulers.io())
                    .mainThread(lifecycle.toFlowable(BackpressureStrategy.BUFFER))
                    .subscribe(::handlePlacesResponse, ::log)
            else
            // clear previously discovered places
                discoveredPlaces.set(mainViewModel.getPersistedPlaces())
        }
    }

    /**
     * User has selected a place from the list of discovered places.
     */
    private fun selectedPlaceUpdated(place: PlaceViewModel) {
        log("SelectedPlaceUpdated:\n$place")
        selectedPlace.set(place)
    }

    /**
     * Update the [MainViewModel] with common data changes.
     */
    private fun setMainViewModelListeners() {
        discoveredPlaces.onChanged(mainViewModel::onPlacesDiscovered)
        persistedPlaces.onChanged(mainViewModel::onPersistedPlacesChanged)
        searchRadius.onChanged(mainViewModel::onSearchRadiusChanged)
        selectedPlace.onChanged { if (it != null) mainViewModel.onPlaceSelected(it) }
    }

    /**
     * Observe category selection changes.
     */
    @SuppressLint("CheckResult")
    private fun setCategorySelectionListener() {
        mainViewModel.observeCategorySelection()
            .subscribeOn(Schedulers.computation())
            .mainThread(lifecycle)
            .subscribe(::selectedCategoriesUpdated, ::log)
    }

    /**
     * Observe place selection changes.
     */
    @SuppressLint("CheckResult")
    private fun setPlaceSelectionListener() {
        mainViewModel.observePlaceSelection()
            .subscribeOn(Schedulers.computation())
            .mainThread(lifecycle)
            .subscribe(::selectedPlaceUpdated, ::log)
    }

    /**
     * Observe location changes.
     */
    @SuppressLint("CheckResult")
    private fun setKnownLocationListener() {
        lastKnownLocation.set(locationService.getLocation())
        locationService.observeLocationChanges()
            .subscribeOn(Schedulers.io())
            .mainThread(lifecycle)
            .subscribe(::handleLocationChanged, ::log)
    }

    /**
     * Receives an updated location, for a valid location attempts to fetch geo data, otherwise clears previous data.
     */
    private fun handleLocationChanged(location: Optional<Location>) {
        location.valueOrNull.let { unBoxedLocation ->
            lastKnownLocation.set(unBoxedLocation)

            // Attempt to retrieve geo data for this location
            if (unBoxedLocation != null)
            // Retrieve an updated address for the new location.
                hereGeoService.getGeoLocation(GeoProximityType(unBoxedLocation.latitude, unBoxedLocation.longitude))
                    .subscribeOn(Schedulers.io())
                    .mainThread(lifecycle)
                    .subscribe(::handleGeoResponse, ::log)
            // Clear last address
            else
                lastKnownAddress.set(null)
        }
    }

    /**
     * Receives a list of places, from the given list creates a list of [PlaceViewModel]'s and updates the
     * [MapFragmentViewModel.discoveredPlaces] member.
     */
    private fun handlePlacesResponse(places: List<Place>) {
        log("Discovered places: [$places]")

        if (places.isEmpty())
            discoveredPlaces.set(null)
        else
            discoveredPlaces.set(
                places.map {
                    PlaceViewModel(dataStore, lifecycle, it).apply {
                        // Update database with refreshed favorite items
                        if (isFavorite.get())
                            savePlace()
                    }
                }
            )
    }

    private fun handleGeoResponse(address: Address) {
        log("Found address: [$address]")
        lastKnownAddress.set(address)
    }

    /**
     * Observe changes to Realm's persisted places.
     */
    @SuppressLint("CheckResult")
    private fun setDataStorePlacesListener() {
        log("Listening to data store changes!")
        dataStore.subscribeToPlaces()
            .subscribeOn(Schedulers.io())
            .mainThread(lifecycle)
            .distinctUntilChanged()
            .subscribe(::handleDataStorePlacesUpdate, ::log)
    }

    /**
     * Receives a most recent copy of Realm's persisted places and merges the data with the discovered places.
     */
    private fun handleDataStorePlacesUpdate(places: List<Place>) {
        log("Updating map with data store values..\n${places.joinToString("\n")}")

        // Create a list from the already discovered places which contains all the non persisted places.
        val nonPersistedPlaces = discoveredPlaces.get()?.toMutableList()?.filter { discoveredPlace ->
            if (discoveredPlace.place in places)
                return@filter false
            else {
                discoveredPlace.isFavorite.set(false)
                return@filter true
            }
        } ?: emptyList()

        // Update the local persisted copy with the updated one after calculating a new distance with current location and sorting.
        persistedPlaces.set(
            places.map(::createPlaceViewModelWithUpdatedDistance)
                .sortedBy { Integer.parseInt(it.distance.get()) }
        )
        // Set discovered places with joined data sets, giving order priority to persisted data.
        discoveredPlaces.set(persistedPlaces.get() + nonPersistedPlaces)
    }

    /**
     * Creates a [PlaceViewModel] from the given [Place], while updating it's data with a current distance calculation.
     */
    private fun createPlaceViewModelWithUpdatedDistance(place: Place): PlaceViewModel {
        return PlaceViewModel(
            dataStore,
            lifecycle,
            place.apply {
                lastKnownLocation.get()?.let { currentLocation ->
                    val placeLocation = Location("").apply {
                        latitude = position[0]
                        longitude = position[1]
                    }
                    distance = currentLocation.distanceTo(placeLocation).roundToInt().toString()
                }
            })
            .also { it.savePlace() }
    }
}