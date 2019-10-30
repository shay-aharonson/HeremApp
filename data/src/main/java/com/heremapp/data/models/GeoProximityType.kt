package com.heremapp.data.models

data class GeoProximityType(
    val lat: Double,
    val lang: Double,

    /**
     * Radius measured in meters.
     */
    val radius: Int = 250
) {
    fun toPlacesApiString(): String {
        return String.format("%s,%s;r=%s", lat, lang, radius)
    }

    fun toGeoApiString(): String {
        return String.format("%s,%s,%s", lat, lang, radius)
    }
}