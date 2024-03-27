package com.example.travel.data.network

import com.example.travel.data.network.dto.CityDTO
import com.example.travel.data.network.dto.DayPlaceDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.dto.TripListDTO
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

//const val BASE_URL = "https://192.168.1.28:3000/"
const val BASE_URL = "http://10.0.2.2:3000/"

interface TravelApi {
    @GET("place")
    suspend fun getPlaces(@Query("cityId") cityId: Int): List<PlaceDTO>

    @POST("trip")
    suspend fun createTrip(@Header("Authorization") token: String, @Body trip: TripDTO)

    @GET("trip")
    suspend fun getTripListByUser(@Header("Authorization") token: String): List<TripListDTO>

    @POST("day-places")
    suspend fun addDayPlace(@Header("Authorization") token: String, @Body dayPlace: DayPlaceDTO)

    @GET("day-places")
    suspend fun getPlaceListByUser(@Header("Authorization") token: String): List<DayPlaceDTO>

    @GET("city")
    suspend fun getCityList(): List<CityDTO>
}

object RetrofitInstance {

    private val interceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val travelApi: TravelApi = retrofit.create(TravelApi::class.java)
}