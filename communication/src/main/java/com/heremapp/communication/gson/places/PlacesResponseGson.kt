package com.heremapp.communication.gson.places

import com.heremapp.data.models.place.Place

/**
{
"results": {
"next": "https://places.cit.api.here.com/places/v1/discover/explore;context=Y2F0PXJlc3RhdXJhbnQmZmxvdy1pZD1jZWRiY2U1ZC04ZGIwLTVjNzEtYjgyOS1kMDE3NmI4MjAwYjFfMTU2MzQ1NTkyNDE2MV8wXzY1NyZvZmZzZXQ9MjAmc2l6ZT0yMA?at=42.260723%2C-71.845324&app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"items": [
{
"position": [
42.263566,
-71.833191
],
"distance": 1047,
"title": "Papa John's Pizza",
"averageRating": 0,
"category": {
"id": "restaurant",
"title": "Restaurant",
"href": "https://places.cit.api.here.com/places/v1/categories/places/restaurant?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"type": "urn:nlp-types:category",
"system": "places"
},
"icon": "https://download.vcdn.cit.data.here.com/p/d/places2_stg/icons/categories/03.icon",
"vicinity": "406 Chandler St<br/>Worcester, MA 01602",
"having": [],
"type": "urn:nlp-types:place",
"href": "https://places.cit.api.here.com/places/v1/places/840drsbm-fb8338b6701440b8a73d68b72f575a87;context=Zmxvdy1pZD1jZWRiY2U1ZC04ZGIwLTVjNzEtYjgyOS1kMDE3NmI4MjAwYjFfMTU2MzQ1NTkyNDE2MV8wXzY1NyZyYW5rPTA?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"tags": [
{
"id": "pizza",
"title": "Pizza",
"group": "cuisine"
}
],
"id": "840drsbm-fb8338b6701440b8a73d68b72f575a87",
"openingHours": {
"text": "Mon-Thu, Sun: 11:00 - 00:00<br/>Fri, Sat: 11:00 - 02:00",
"label": "Opening hours",
"isOpen": false,
"structured": [
{
"start": "T110000",
"duration": "PT13H00M",
"recurrence": "FREQ:DAILY;BYDAY:MO,TU,WE,TH,SU"
},
{
"start": "T110000",
"duration": "PT15H00M",
"recurrence": "FREQ:DAILY;BYDAY:FR,SA"
}
]
},
"chainIds": [
"1578"
]
},
{
"position": [
42.26406,
-71.83344
],
"distance": 1046,
"title": "Friends Cafe",
"averageRating": 0,
"category": {
"id": "restaurant",
"title": "Restaurant",
"href": "https://places.cit.api.here.com/places/v1/categories/places/restaurant?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"type": "urn:nlp-types:category",
"system": "places"
},
"icon": "https://download.vcdn.cit.data.here.com/p/d/places2_stg/icons/categories/03.icon",
"vicinity": "120 June St<br/>Worcester, MA 01602",
"having": [],
"type": "urn:nlp-types:place",
"href": "https://places.cit.api.here.com/places/v1/places/840drsbm-effb2eed16b845ab940e1401745319d2;context=Zmxvdy1pZD1jZWRiY2U1ZC04ZGIwLTVjNzEtYjgyOS1kMDE3NmI4MjAwYjFfMTU2MzQ1NTkyNDE2MV8wXzY1NyZyYW5rPTE?app_id=W1dlovEoDp0JjCSeQCQ2&app_code=wsN3rKGoeB6GuTLmitchPg",
"id": "840drsbm-effb2eed16b845ab940e1401745319d2",
"openingHours": {
"text": "Mon-Sun: 07:00 - 15:00",
"label": "Opening hours",
"isOpen": true,
"structured": [
{
"start": "T070000",
"duration": "PT08H00M",
"recurrence": "FREQ:DAILY;BYDAY:MO,TU,WE,TH,FR,SA,SU"
}
]
}
},
 ...
 ...
 }
 */
data class PlacesResponseGson(
    val results: PlacesResponse
) {
    data class PlacesResponse(
        val next: String,
        val items: List<Place>
    )
}