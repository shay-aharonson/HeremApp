package com.heremapp.data.persistedmodels.persistedplace

import com.heremapp.data.models.place.Tag
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PersistedTag(
    @PrimaryKey
    @Required
    var id: String = "",
    var title: String = "",
    var group: String = ""
) : RealmObject() {

    constructor(tag: Tag) : this(
        id = tag.id,
        title = tag.title,
        group = tag.group
    )
}