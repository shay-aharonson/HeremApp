package com.heremapp.communication.places

import android.annotation.SuppressLint
import com.heremapp.communication.gson.places.AdjacentPlacesResponseGson
import com.heremapp.communication.gson.places.PlacesResponseGson.PlacesResponse
import com.heremapp.data.PersistedDataStore
import com.heremapp.data.models.Category
import com.heremapp.data.models.GeoProximityType
import com.heremapp.data.models.place.Place
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.Optional
import com.heremapp.utility.rx.bindUntil
import com.heremapp.utility.splitFiltered
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Data source of [Place]'s, provided from [HerePlacesService] & [HerePlacesApi]. Loads all places associated to a
 * given list of [Category] items and [GeoProximityType]. Response is retrieved in paginated structures and is exposed
 * to the caller by the [placesSubject] hot observable.
 */
class PlacesDataSource(
    private val lifecycle: Observable<FragmentEvent>,
    private val herePlacesService: HerePlacesService,
    private val dataStore: PersistedDataStore
) {

    private lateinit var placesSubject: BehaviorSubject<List<Place>>

    private lateinit var geoProximityType: GeoProximityType
    private lateinit var categories: List<Category>

    @SuppressLint("CheckResult")
    fun loadPlaces(geoProximityType: GeoProximityType, categories: List<Category>): Observable<List<Place>> {
        this.geoProximityType = geoProximityType
        this.categories = categories
        placesSubject = BehaviorSubject.create<List<Place>>()

        herePlacesService.getPlaces(geoProximityType, categories)
            .bindUntil(lifecycle)
            .subscribeOn(Schedulers.io()) // Subscribe on IO to fetch initial places.
            .toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(AndroidSchedulers.mainThread()) // Observe on UI to allow Realm to update info.
            .map(::handleInitialResponse)
            .filter { !it.isNull }
            .map { it.get() }
            .observeOn(Schedulers.io()) // Observe on IO to fetch additional places.
            .flatMap(::loadAdjacent)
            .observeOn(AndroidSchedulers.mainThread()) // Observe again on UI to allow Realm to update info.
            .subscribe(::handleAdjacentResponse, ::log)

        return placesSubject
    }

    /**
     * Emit a list of retrieved places and return the next page to load.
     * Handle initial response separately from adjacent due to difference in api structure.
     */
    private fun handleInitialResponse(response: PlacesResponse): Optional<String?> {
        log("Initial response: $response")
        placesSubject.onNext(mergeDataStorePlaces(response.items))
        return Optional(response.next)
    }

    /**
     * Load next pages in recursion till all pages have been loaded.
     */
    private fun loadAdjacent(nextUrl: String): Flowable<AdjacentPlacesResponseGson> {
        log("Next url: $nextUrl")
        return herePlacesService.getAdjacentPlaces(nextUrl)
            .toFlowable()
            .concatMap { response ->
                if (response.next.isNullOrEmpty())
                    Flowable.just(response)
                else
                    Flowable.just(response)
                        .concatWith(loadAdjacent(response.next))
            }
    }

    /**
     * Add response places to the existing list of retrieved places and emit an update.
     */
    private fun handleAdjacentResponse(response: AdjacentPlacesResponseGson) {
        placesSubject.onNext(
            placesSubject.value!!
                .toMutableList()
                .apply {
                    addAll(mergeDataStorePlaces(response.items))
                })
    }

    /**
     * Merge API results with existing persisted data.
     */
    private fun mergeDataStorePlaces(apiPlaces: List<Place>): List<Place> {
        log("Merging search results with data store..")
        // List of persisted places that have not been updated by the API
        val filteredPersistedPlaces = dataStore.getLoadedPlaces().toMutableList()
        // List of fetched API places that are not persisted.
        val filteredApiPlaces = apiPlaces.toMutableList()
        /*
         * List of persisted places that have been re-fetched by the API.
         * Filter out all api places that are also persisted, while updating the new api data with persisted data.
         */
        val updatedPlaces = filteredApiPlaces.splitFiltered { apiPlace ->
            dataStore.getLoadedPlaces().any { dataPlace ->
                if (dataPlace == apiPlace) {
                    apiPlace.isFavorite = true
                    apiPlace.notes = dataPlace.notes
                    // Remove this place from the persisted list since it will be returned by .splitFiltered into updatedPlaces.
                    filteredPersistedPlaces.remove(dataPlace)
                    return@splitFiltered true
                } else
                    // Places without a match will be returned by .splitFiltered into filteredApiPlaces.
                    return@any false
            }
        }

        log("filteredPersistedPlaces:\n${filteredPersistedPlaces.joinToString("\n")}")
        log("updatedPlaces:\n${updatedPlaces.joinToString("\n")}")
        log("filteredApiPlaces:\n${filteredApiPlaces.joinToString("\n")}")

        return (filteredPersistedPlaces + updatedPlaces).sortedBy { Integer.parseInt(it.distance) } + filteredApiPlaces
    }
}