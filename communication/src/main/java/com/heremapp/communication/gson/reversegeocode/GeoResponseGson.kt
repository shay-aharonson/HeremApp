package com.heremapp.communication.gson.reversegeocode

import com.google.gson.annotations.SerializedName
import com.heremapp.data.models.place.Address

/**
```
{
"PlacesResponse": {
"MetaInfo": {
"Timestamp": "2019-06-26T18:42:13.787+0000",
"NextPageInformation": "2"
},
"View": [
{
"_type": "SearchResultsViewType",
"ViewId": 0,
"Result": [
{
"Relevance": 1,
"Distance": 13.6,
"MatchLevel": "houseNumber",
"MatchQuality": {
"Country": 1,
"State": 1,
"County": 1,
"City": 1,
"District": 1,
"Street": [
1
],
"HouseNumber": 1,
"PostalCode": 1
},
"MatchType": "pointAddress",
"Location": {
"LocationId": "NT_Opil2LPZVRLZjlWNLJQuWB_0ITN",
"LocationType": "address",
"DisplayPosition": {
"Latitude": 41.88432,
"Longitude": -87.63877
},
"NavigationPosition": [
{
"Latitude": 41.88449,
"Longitude": -87.63877
}
],
"MapView": {
"TopLeft": {
"Latitude": 41.8854442,
"Longitude": -87.64028
},
"BottomRight": {
"Latitude": 41.8831958,
"Longitude": -87.63726
}
},
"Address": {
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
},
"MapReference": {
"ReferenceId": "776372180",
"MapId": "NAAM191F0",
"MapVersion": "Q1/2019",
"MapReleaseDate": "2019-06-05",
"Spot": 0.52,
"SideOfStreet": "right",
"CountryId": "21000001",
"StateId": "21002247",
"CountyId": "21002623",
"CityId": "21002647",
"BuildingId": "9000000000002726912",
"AddressId": "79186508",
"RoadLinkId": "170008450"
}
}
}
]
}
]
}
}
```
 */
data class GeoResponseGson(
    @SerializedName("Response")
    val response: Response
) {

    data class Response(
        @SerializedName("View")
        val views: List<GeoView>?
    )

    data class GeoView(
        @SerializedName("Result")
        val results: List<GeoResult>?
    )

    data class GeoResult(
        @SerializedName("Location")
        val location: GeoLocation?
    )

    data class GeoLocation(
        @SerializedName("Address")
        val address: Address?
    )
}