package com.heremapp.communication.geo

import com.heremapp.communication.gson.reversegeocode.GeoResponseGson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interface for /geo
 */
interface HereGeoApi {

    @GET("reversegeocode.json")
    fun getGeo(
        @Query("prox") prox: String,
        @Query("mode") mode: String = "retrieveAddresses",
        @Query("maxresults") maxResults: Int = 1,
        @Query("gen") gen: Int = 9,
        @Query("app_id") appId: String,
        @Query("app_code") appCode: String
    ): Single<GeoResponseGson>
}