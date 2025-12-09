package com.example.leadflow.network

import com.example.leadflow.data.SmsResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("get-sms")
    suspend fun sendScannedUrl(
        @Query("url") scannedUrl: String
    ): Response<SmsResponse>

    @FormUrlEncoded
    @POST
    suspend fun sendSmsViaTwilio(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Field("To") to: String,
        @Field("From") from: String,
        @Field("Body") body: String
    ) : Response<ResponseBody>
}

object RetrofitInstance {
    private const val BASE_URL = "http://13.62.99.125:8000/"
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 3. Retrofit ko ye client use karne ko bolo
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // <--- Ye line zaroori hai
            .build()
            .create(ApiService::class.java)
    }
}

