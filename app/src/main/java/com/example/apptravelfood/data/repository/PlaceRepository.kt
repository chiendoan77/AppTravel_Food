package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.remote.api.SepriAPI
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

class PlaceRepository(
    private val api: SepriAPI
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