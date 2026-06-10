package com.example.apptravelfood.data.repository

import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.remote.api.SepriAPI
import com.example.apptravelfood.domain.model.AddressSuggestion

class AddressRepository(
    private val api: SepriAPI
) {
    suspend fun searchAddress(
        query: String,
        province: String = "Quy Nhơn, Bình Định, Việt Nam"
    ): List<AddressSuggestion> {
        val response = api.searchAddress(
            query = "$query, $province",
            apiKey = AppConstant.API_KEY
        )

        return response.local_results
            ?.mapNotNull { result ->
                val lat = result.gps_coordinates?.latitude
                val lng = result.gps_coordinates?.longitude
                val address = result.address ?: result.title

                if (lat == null || lng == null || address == null) {
                    null
                } else {
                    AddressSuggestion(
                        title = result.title ?: address,
                        address = address,
                        latitude = lat,
                        longitude = lng
                    )
                }
            }
            ?: emptyList()
    }
}