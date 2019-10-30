package com.heremapp.data.models.place

import com.heremapp.data.persistedmodels.persistedplace.PersistedPlace

data class Place(

    var position: DoubleArray = DoubleArray(2),

    var distance: String = "",

    var title: String = "",

    var icon: String = "",

    var vicinity: String = "",

    var id: String = "",

    var tags: List<Tag> = emptyList(),

    var openingHours: OpeningHours = OpeningHours(),

    var isFavorite: Boolean = false,

    var notes: String = ""
) {

    constructor(persistedPlace: PersistedPlace) : this(
        position = doubleArrayOf(persistedPlace.persistedPosition[0] ?: 0.0, persistedPlace.persistedPosition[1] ?: 0.0),
        distance = persistedPlace.distance,
        title = persistedPlace.title,
        icon = persistedPlace.icon,
        vicinity = persistedPlace.vicinity,
        id = persistedPlace.id,
        tags = persistedPlace.persistedTags
            .map(::Tag),
        openingHours = persistedPlace.persistedOpeningHours?.let(::OpeningHours) ?: OpeningHours(),
        isFavorite = true,
        notes = persistedPlace.notes)

    fun toPersistedClass(): PersistedPlace {
        return PersistedPlace(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Place) return other.id == this.id
        return super.equals(other)
    }
}