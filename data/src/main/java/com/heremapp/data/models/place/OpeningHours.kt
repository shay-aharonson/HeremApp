package com.heremapp.data.models.place

import androidx.core.text.HtmlCompat
import com.heremapp.data.persistedmodels.persistedplace.PersistedOpeningHours

class OpeningHours(
    private var text: String = "",
    var label: String = "",
    var isOpen: Boolean = false
) {
    constructor(persistedOpeningHours: PersistedOpeningHours) : this(
        text = persistedOpeningHours.text,
        label = persistedOpeningHours.label,
        isOpen = persistedOpeningHours.isOpen
    )

    override fun toString(): String {
        return "${if (isOpen) "Open" else "Closed"}\n${getTextString()}"
    }

    fun getTextString(): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    }

    fun getTextData(): String {
        return text
    }
}