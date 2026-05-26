package com.example.apptravelfood.data.reponsitory

import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.remote.api.APIinterface
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

class PlaceRepository(
    private val api: APIinterface
) {
    suspend fun sreachPlaces(
        query: String,
        location: String,
        apiKey: String
    ): List<LocalResultsDto> {
        val response = api.searchPlaces(
            query = query,
            location = location,
            apiKey = apiKey
        )
        return response.local_results
    }
}