package com.heremapp.presentation.search

import androidx.databinding.ObservableField
import com.heremapp.communication.places.HerePlacesService
import com.heremapp.data.models.Category
import com.heremapp.data.models.GeoProximityType
import com.heremapp.presentation.base.LoadingViewModel
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.STATUS_ALL
import com.heremapp.presentation.communication.CommunicationSupervisorViewModel.Companion.STATUS_NO_LOCATION
import com.heremapp.presentation.extensions.NonNullObservableField
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.CategoryViewModel
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.bindUntil
import com.location.LocationService
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.roundToInt

/**
 * Handle logic for the search screen, will provide data of all category types of nearby places of interest.
 */
class SearchFragmentViewModel(communicationSupervisorViewModel: CommunicationSupervisorViewModel,
                              private val placesService: HerePlacesService,
                              private val locationService: LocationService,
                              private val lifecycle: Observable<FragmentEvent>,
                              private val mainViewModel: MainViewModel): LoadingViewModel() {

    // All retrieved categories of nearby places
    val categories = NonNullObservableField<List<CategoryViewModel>>(emptyList())

    // Categories that were selected by the user
    val selectedCategories = ObservableField<List<CategoryViewModel>>(emptyList())

    init {
        onViewDisplayed()
        communicationSupervisorViewModel.communicationStatus.onChanged { status ->
            if (status == STATUS_ALL || status == STATUS_NO_LOCATION)
                onViewDisplayed()
        }
    }

    /**
     * Update the selected category list with the newly selected ones.
     */
    fun onCategorySelected(selectedCategories: List<CategoryViewModel>?) {
        this.selectedCategories.set(selectedCategories)
        mainViewModel.onCategoriesSelected(selectedCategories)
    }

    /**
     * Attempt to refresh categories when the view is displayed. This is necessary since the onResume() won't be called
     * after the initial display.
     */
    fun onViewDisplayed() {
        log("Refreshing categories..")
        fetchCategories()
    }

    /**
     * Fetch all categories via [HerePlacesService] in the given location.
     */
    private fun fetchCategories() {
        locationService.getLocation()?.let { location ->
            mainViewModel.observeSearchRadius()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .bindUntil(lifecycle)
                .firstOrError()
                .flatMap { radius ->
                    placesService
                        .getCategories(GeoProximityType(location.latitude, location.longitude, radius.roundToInt()))
                }
                .filter { !categories.get().equalsCategories(it) } // Only update if result differs from current data.
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingState.set(STATE_LOADING) }
                .doFinally { loadingState.set(STATE_COMPLETE) }
                .subscribe(::handleCategoriesResponse, ::log)
        }
    }

    private fun handleCategoriesResponse(categories: List<Category>) {
        log("Updated categories: [$categories]")
        this.categories.set(categories.map(::CategoryViewModel))
    }
}

/**
 * Add a list extension for comparing a list of category viewModels with a list of categories, to determine if the
 * viewModels contain the categories and no more.
 */
fun List<CategoryViewModel>.equalsCategories(categories: List<Category>): Boolean {
    return categories == this.map { it.category }
}