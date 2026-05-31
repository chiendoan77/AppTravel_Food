package com.example.apptravelfood.core.constant

object AppConstant {
    const val BASE_URL = "https://serpapi.com/"
    const val API_KEY = "b4f776e02e90c69d0b90f30ad655e890c7d3045b085b40bacf8f54cd4616bd6d"
//    const val API_KEY =""

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
