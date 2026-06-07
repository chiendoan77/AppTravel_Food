package com.example.apptravelfood.data.remote.dto

data class SerpResponseDto(
    val local_results: List<LocalResultsDto>
)
data class GpsCoordinatesDto(
    val latitude: Double,
    val longitude: Double
)

data class LinkDto(
    val directions: String?,
)

data class LocalResultsDto(
    val rating: Double?,
    val description: String?,
    val title: String,
    val place_id: String,
    val type: String,
    val address: String?,
    val gps_coordinates: GpsCoordinatesDto?,
    val links: LinkDto?,
    val thumbnail_large: String?
)
