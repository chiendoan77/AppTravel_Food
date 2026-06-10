package com.example.apptravelfood.data.remote.dto

data class GoogleMapsResponseDto(
    val local_results: List<GoogleMapsResultDto>? = null
)

data class GoogleMapsResultDto(
    val title: String? = null,
    val address: String? = null,
    val gps_coordinates: GpsCoordinatesDto? = null
)