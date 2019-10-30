package com.heremapp.presentation.main

import android.annotation.SuppressLint
import androidx.core.text.HtmlCompat
import androidx.databinding.ObservableField
import com.google.android.gms.maps.model.LatLng
import com.heremapp.data.PersistedDataStore
import com.heremapp.data.models.place.OpeningHours
import com.heremapp.data.models.place.Place
import com.heremapp.data.models.place.Tag
import com.heremapp.presentation.extensions.NonNullObservableField
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for view representing the [Place] data set, also manages the persistence of this place's data to Realm.
 */
class PlaceViewModel(private val dataStore: PersistedDataStore, private val lifecycle: Observable<FragmentEvent>,
                     val place: Place) {

    val id = NonNullObservableField("")
    val title = NonNullObservableField("")
    val position = NonNullObservableField(LatLng(0.0, 0.0))
    val vicinity = NonNullObservableField("")
    val distance = NonNullObservableField("")
    val icon = NonNullObservableField("")
    val isFavorite = NonNullObservableField(false)
    val tags = ObservableField<List<Tag>>()
    val openingHours = ObservableField<OpeningHours>()
    val notes = NonNullObservableField("")

    init {
        position.set(LatLng(place.position[0], place.position[1]))
        distance.set(place.distance)
        title.set(place.title)
        icon.set(place.icon)
        vicinity.set(HtmlCompat.fromHtml(place.vicinity, HtmlCompat.FROM_HTML_MODE_COMPACT).toString())
        id.set(place.id)
        tags.set(place.tags)
        openingHours.set(place.openingHours)
        isFavorite.set(place.isFavorite)
        notes.set(place.notes)

        notes.onChanged {
            place.notes = it

            if (isFavorite.get())
                savePlace()
        }
    }

    override fun toString(): String {
        return listOf(id.get(), title.get(), position.get(), vicinity.get(), distance.get(), icon.get(), openingHours.get(), tags.get()).joinToString(separator = ",\n")
    }

    /**
     * Simple equality check based on class type and id member.
     */
    override fun equals(other: Any?): Boolean {
        if (other is PlaceViewModel)
            return this.id.get() == (other.id.get())
        return super.equals(other)
    }

    /**
     * Attempt to save or delete this data given the toggled state.
     */
    fun onFavoriteClicked() {
        isFavorite.get().not().let { valueToSet ->
            if (valueToSet)
                savePlace()
            else
                deletePlace()
        }
    }

    fun getTagsString(): String {
        return tags.get()?.joinToString { it.title } ?: ""
    }

    fun getOpenHoursString(): String {
        return openingHours.get()?.toString() ?: ""
    }

    /**
     * Save the represented [Place] data to Realm, if successful sets to favorite state.
     */
    @SuppressLint("CheckResult")
    fun savePlace() {
        log("Saving..")
        dataStore.savePlace(place)
            .subscribeOn(Schedulers.io())
            .mainThread(lifecycle)
            .subscribe({ place ->
                if (place != null) {
                    log("Place data saved: $place")
                    isFavorite.set(true)
                }
            }, ::log)
    }

    /**
     * Delete the represented [Place] data from Realm, if successful sets to non-favorite state.
     */
    @SuppressLint("CheckResult")
    fun deletePlace() {
        log("Deleting..")
        dataStore.deletePlace(place)
            .subscribeOn(Schedulers.io())
            .mainThread(lifecycle)
            .subscribe({ isDeleted ->
                if (isDeleted) {
                    log("Place data deleted.")
                    isFavorite.set(false)
                }
            }, ::log)
    }
}