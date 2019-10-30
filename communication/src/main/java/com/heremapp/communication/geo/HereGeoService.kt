package com.heremapp.communication.geo

import com.heremapp.communication.ApiConstants.HERE_APP_CODE
import com.heremapp.communication.ApiConstants.HERE_APP_ID
import com.heremapp.data.models.GeoProximityType
import com.heremapp.data.models.place.Address
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service which handles all HERE /geo API's
 */
@Singleton
class HereGeoService @Inject constructor(){

    class GeoLocationNotFoundException : RuntimeException("Geo location empty!")

    private val hereGeoApi: HereGeoApi = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://reverse.geocoder.api.here.com/6.2/")
        .client(
            OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build())
        .build()
        .create(HereGeoApi::class.java)

    /**
     * Get the [Address] for the given [GeoProximityType].
     */
    fun getGeoLocation(proximityType: GeoProximityType): Single<Address> {
        return hereGeoApi.getGeo(prox = proximityType.toGeoApiString(), appId = HERE_APP_ID, appCode = HERE_APP_CODE)
            .map {
                it.response.views?.getOrNull(0)?.results?.getOrNull(0)?.location?.address
                    ?: throw GeoLocationNotFoundException()
            }
    }
}