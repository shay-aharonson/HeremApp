package com.heremapp.data.models.place

import com.heremapp.data.persistedmodels.persistedplace.PersistedTag

class Tag (
    var id: String = "",
    var title: String = "",
    var group: String = ""
) {
    constructor(persistedTag: PersistedTag) : this(
        id = persistedTag.id,
        title = persistedTag.title,
        group = persistedTag.group)
}