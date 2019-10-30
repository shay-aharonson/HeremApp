package com.heremapp.communication.places

import com.heremapp.communication.ApiConstants.HERE_APP_CODE
import com.heremapp.communication.ApiConstants.HERE_APP_ID
import com.heremapp.communication.gson.places.AdjacentPlacesResponseGson
import com.heremapp.communication.gson.places.PlacesResponseGson.PlacesResponse
import com.heremapp.data.models.Category
import com.heremapp.data.models.GeoProximityType
import com.heremapp.data.models.place.Place
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service which handles communication with the HERE /places API.
 */
@Singleton
class HerePlacesService @Inject constructor() {

    private val herePlacesApi: HerePlacesApi = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://places.cit.api.here.com/places/v1/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()
        .create(HerePlacesApi::class.java)

    /**
     * Get all available [Category]'s within the given [GeoProximityType].
     */
    fun getCategories(geoProximityType: GeoProximityType): Single<List<Category>> {
        return herePlacesApi.getCategories(geoProximityType.toPlacesApiString(), appId = HERE_APP_ID, appCode = HERE_APP_CODE)
            .map { it.categories }
    }

    /**
     * A pagination api to fetch additional [Place]'s from the [HerePlacesService.getPlaces] call, url provided in the
     * response.
     */
    fun getAdjacentPlaces(url: String): Single<AdjacentPlacesResponseGson> {
        return herePlacesApi.getAdjacentPlaces(url)
    }

    /**
     * Get all [Place]'s associated with the given [Category]'s within the given [GeoProximityType].
     * Returns a paginated response when successful
     */
    fun getPlaces(geoProximityType: GeoProximityType, categories: List<Category>): Single<PlacesResponse> {
        return herePlacesApi.getPlaces(
            categories.joinToString(",") { it.id },
            geoProximityType.toPlacesApiString(), HERE_APP_ID, HERE_APP_CODE
        ).map { it.results }
    }
}