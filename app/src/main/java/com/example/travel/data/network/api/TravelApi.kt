package com.example.travel.data.network.api

import com.example.travel.data.network.dto.AudioDTO
import com.example.travel.data.network.dto.CityDTO
import com.example.travel.data.network.dto.CreateReviewDTO
import com.example.travel.data.network.dto.DayPlaceDTO
import com.example.travel.data.network.dto.LoginDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.data.network.dto.CreateSubscribeDTO
import com.example.travel.data.network.dto.GetReviewDTO
import com.example.travel.data.network.dto.GetSubscribeDTO
import com.example.travel.data.network.dto.TokenDTO
import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.dto.TripListDTO
import com.example.travel.data.network.dto.TypeSubDTO
import com.example.travel.data.network.dto.UserDTO
import com.example.travel.data.network.dto.UserProfileDTO
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.UpdateScoresRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

//const val BASE_URL = "https://192.168.1.28:3000/"
//const val BASE_URL = "http://10.0.2.2:3000/"
const val BASE_URL = "https://de86-188-66-38-126.ngrok-free.app/"
//const val BASE_URL = "http://192.168.111.242:3000/"

interface TravelApi {
    @POST("auth/register")
    suspend fun register(@Body user: UserDTO)

    @POST("auth/login")
    suspend fun login(@Body user: LoginDTO): TokenDTO

    @GET("place")
    suspend fun getPlaces(
        @Query("cityId") cityId: Int,
        @Query("search") search: String,
        @Query("category") category: String,
    ): List<PlaceDTO>

    @GET("place/category")
    suspend fun getCategoryList(): List<CategoryModel>

    @GET("place/{id}")
    suspend fun getPlaceById(@Path("id") id: String): PlaceDTO

    @POST("trip")
    suspend fun createTrip(@Header("Authorization") token: String, @Body trip: TripDTO)

    @GET("trip")
    suspend fun getTripListByUser(@Header("Authorization") token: String): List<TripListDTO>

    @POST("day-places")
    suspend fun addDayPlace(@Header("Authorization") token: String, @Body dayPlace: DayPlaceDTO)

    @GET("day-places")
    suspend fun getPlaceListByUser(@Header("Authorization") token: String,
                                   @Query("date") date: String): List<DayPlaceDTO>

    @GET("city")
    suspend fun getCityList(): List<CityDTO>

    @GET("audio/{id}")
    suspend fun getAudioListByPlace(@Path("id") placeId: String): List<AudioDTO>

    @GET("audio")
    suspend fun getAudioList(): List<AudioDTO>

    @GET("auth/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): UserProfileDTO

    @GET("auth/users")
    suspend fun getUserList(): List<UserProfileDTO>

    @GET("type-sub")
    suspend fun getTypeSubList(): List<TypeSubDTO>

    @GET("type-sub/{id}")
    suspend fun getTypeSubById(@Path("id") id: Int): TypeSubDTO

    @POST("subscribe")
    suspend fun createSubscribe(@Header("Authorization") token: String, @Body subscribe: CreateSubscribeDTO)

    @GET("subscribe")
    suspend fun getSubscribeListByUser(@Header("Authorization") token: String): List<GetSubscribeDTO>

    @POST("review")
    suspend fun createReview(@Header("Authorization") token: String, @Body review: CreateReviewDTO)

    @GET("review")
    suspend fun getReviewListByUser(@Header("Authorization") token: String): List<GetReviewDTO>

    @GET("review/{id}")
    suspend fun getReviewListByPlace(@Path("id") id: String): List<GetReviewDTO>

    @PATCH("auth/scores")
    suspend fun updateScoresFromApi(@Header("Authorization") token: String, @Body() scores: UpdateScoresRequest)
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
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val travelApi: TravelApi = retrofit.create(TravelApi::class.java)
}