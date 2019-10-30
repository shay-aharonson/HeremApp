package com.heremapp.data.persistedmodels.persistedplace

import com.heremapp.data.models.place.Place
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PersistedPlace() : RealmObject() {
    var persistedPosition: RealmList<Double> = RealmList()

    var distance: String = ""

    var title: String = ""

    var icon: String = ""

    var vicinity: String = ""

    @PrimaryKey
    @Required
    var id: String = ""

    var persistedTags: RealmList<PersistedTag> = RealmList()

    var persistedOpeningHours: PersistedOpeningHours? = null

    var notes: String = ""

    constructor(place: Place) : this() {
        persistedPosition.add(0, place.position[0])
        persistedPosition.add(1, place.position[1])
        distance = place.distance
        title = place.title
        icon = place.icon
        vicinity = place.vicinity
        id = place.id
        place.tags
            .map(::PersistedTag)
            .let(persistedTags::addAll)
        persistedOpeningHours = PersistedOpeningHours(place.openingHours, id)
        notes = place.notes
    }

    override fun toString(): String {
        return String.format("{position : %s, distance : %s, title : %s, icon : %s, vicinity : %s, id : %s, persistedTags : %s, hours : %s, notes : %s}",
            persistedPosition, distance, title, icon, vicinity, id, persistedTags, persistedOpeningHours, notes)
    }
}