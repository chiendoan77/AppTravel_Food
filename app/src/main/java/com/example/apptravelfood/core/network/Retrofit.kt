package com.example.apptravelfood.core.network

import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.remote.api.APIinterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val api: APIinterface by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIinterface::class.java)
    }
}