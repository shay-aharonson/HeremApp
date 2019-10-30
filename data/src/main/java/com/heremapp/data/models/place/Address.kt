package com.heremapp.data.models.place

import android.text.TextUtils.isEmpty
import com.google.gson.annotations.SerializedName

/**
"Label": "425 W Randolph St, Chicago, IL 60606, United States",
"Country": "USA",
"State": "IL",
"County": "Cook",
"City": "Chicago",
"District": "West Loop",
"Street": "W Randolph St",
"HouseNumber": "425",
"PostalCode": "60606",
"AdditionalData": [
    {
        "value": "United States",
        "key": "CountryName"
    },
    {
        "value": "Illinois",
        "key": "StateName"
    },
    {
        "value": "Cook",
        "key": "CountyName"
    },
    {
        "value": "N",
        "key": "PostalCodeType"
    }
]
 */
data class Address(
    @SerializedName("Label")
    val label: String,

    @SerializedName("Country")
    val country: String,

    @SerializedName("State")
    val state: String,

    @SerializedName("County")
    val county: String,

    @SerializedName("City")
    val city: String,

    @SerializedName("District")
    val district: String,

    @SerializedName("Street")
    val street: String,

    @SerializedName("HouseNumber")
    val houseNumber: String,

    @SerializedName("PostalCode")
    val postalCode: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "")

    fun toStreetAddressString(): String {
        return listOf(houseNumber, street, city, state, postalCode)
            .filterNot(::isEmpty)
            .joinToString()
    }
}