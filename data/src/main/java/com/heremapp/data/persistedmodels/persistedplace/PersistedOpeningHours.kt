package com.heremapp.data.persistedmodels.persistedplace

import com.heremapp.data.models.place.OpeningHours
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PersistedOpeningHours(
    @PrimaryKey
    @Required
    var id: String = "",
    var text: String = "",
    var label: String = "",
    var isOpen: Boolean = false
) : RealmObject() {

    constructor(openingHours: OpeningHours, id: String): this(
        id = id,
        text = openingHours.getTextData(),
        label = openingHours.label,
        isOpen = openingHours.isOpen)
}