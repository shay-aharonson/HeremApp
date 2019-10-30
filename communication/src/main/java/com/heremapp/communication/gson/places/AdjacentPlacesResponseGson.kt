package com.heremapp.communication.gson.places

import com.heremapp.data.models.place.Place

/**
{
"previous": "https://places.cit.api.here.com/places/v1/discover/explore;context=Zmxvdy1pZD1hYWMwZjIxMi1kNTRjLTU2NDYtYmQyNi04MWZlNzI0OTlmNzRfMTU2MzQ1NjA1ODM2M18wXzM3NjEmb2Zmc2V0PTAmc2l6ZT0yMA?at=52.5159%2C13.3777&app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"next": "https://places.cit.api.here.com/places/v1/discover/explore;context=Zmxvdy1pZD1hYWMwZjIxMi1kNTRjLTU2NDYtYmQyNi04MWZlNzI0OTlmNzRfMTU2MzQ1NjA1ODM2M18wXzM3NjEmb2Zmc2V0PTQwJnNpemU9MjA?at=52.5159%2C13.3777&app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"offset": 20,
"items": [
{
"position": [
52.51043,
13.37318
],
"distance": 681,
"title": "Lindenbrau am Potsdamer Platz",
"averageRating": 0,
"category": {
"id": "restaurant",
"title": "Restaurant",
"href": "https://places.cit.api.here.com/places/v1/categories/places/restaurant?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"type": "urn:nlp-types:category",
"system": "places"
},
"icon": "https://download.vcdn.cit.data.here.com/p/d/places2_stg/icons/categories/03.icon",
"vicinity": "Bellevuestraße 3<br/>Tiergarten, 10785 Berlin",
"having": [],
"type": "urn:nlp-types:place",
"href": "https://places.cit.api.here.com/places/v1/places/276u33d8-2884e07ac5fb47e8bbd3aea50e8ed4af;context=Zmxvdy1pZD1hYWMwZjIxMi1kNTRjLTU2NDYtYmQyNi04MWZlNzI0OTlmNzRfMTU2MzQ1NjA1ODM2M18wXzM3NjEmcmFuaz0yMA?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"tags": [
{
"id": "european",
"title": "European",
"group": "cuisine"
},
{
"id": "german",
"title": "German",
"group": "cuisine"
}
],
"id": "276u33d8-2884e07ac5fb47e8bbd3aea50e8ed4af",
"openingHours": {
"text": "Mon-Sun: 11:30 - 01:00",
"label": "Opening hours",
"isOpen": true,
"structured": [
{
"start": "T113000",
"duration": "PT13H30M",
"recurrence": "FREQ:DAILY;BYDAY:MO,TU,WE,TH,FR,SA,SU"
}
]
},
"alternativeNames": [
{
"name": "Lindenbräu",
"language": "de"
}
]
},
....
....
....
 }
 */
data class AdjacentPlacesResponseGson(
    val previous: String?,
    val next: String?,
    val offset: Int,
    val items: List<Place>
)