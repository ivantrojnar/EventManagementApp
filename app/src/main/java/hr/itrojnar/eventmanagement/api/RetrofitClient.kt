package hr.itrojnar.eventmanagement.api

import hr.itrojnar.eventmanagement.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val serverIpAddress = Constants.SERVER_IP
    private const val serverPort = Constants.SERVER_PORT
    private const val BASE_URL = "http://$serverIpAddress:$serverPort"

    private val okHttpClient = OkHttpClient.Builder().build()

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}