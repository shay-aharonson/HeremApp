package com.heremapp.communication.places

import com.heremapp.communication.gson.places.AdjacentPlacesResponseGson
import com.heremapp.communication.gson.places.CategoriesResponseGson
import com.heremapp.communication.gson.places.PlacesResponseGson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Interface for the /places API.
 */
interface HerePlacesApi {

    @GET
    fun getAdjacentPlaces(@Url url: String): Single<AdjacentPlacesResponseGson>

    @GET("discover/explore")
    fun getPlaces(
        @Query("cat") categories: String? = null,
        @Query("in") geoData: String,
        @Query("app_id") appId: String,
        @Query("app_code") appCode: String
    ): Single<PlacesResponseGson>

    @GET("categories/places")
    fun getCategories(
        @Query("in") geoData: String,
        @Query("app_id") appId: String,
        @Query("app_code") appCode: String
    ): Single<CategoriesResponseGson>

}