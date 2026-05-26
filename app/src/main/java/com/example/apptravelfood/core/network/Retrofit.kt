package com.example.apptravelfood.core.network


import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.remote.api.APIinterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: APIinterface by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIinterface::class.java)
    }
}