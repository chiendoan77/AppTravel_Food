package com.example.apptravelfood.core.constant

import com.example.apptravelfood.BuildConfig

object AppConstant {
    val API_KEY: String = BuildConfig.SERP_API_KEY

    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY: String = BuildConfig.SUPABASE_ANON_KEY
    val OTP_BASE_URL: String = BuildConfig.OTP_BASE_URL

    private val LOCATION_MAP = mapOf(
        "Huế" to "Hue, Vietnam",
        "Thừa Thiên Huế" to "Hue, Vietnam",
        "Đà Nẵng" to "Da Nang, Vietnam",
        "Hà Nội" to "Hanoi, Vietnam",
        "Gia Lai" to "Quy Nhon, Binh Djinh, Vietnam"
    )
    fun getLocationFromProvince(
        province: String?
    ): String {

        return LOCATION_MAP[province]
            ?: "Vietnam"
    }

}
