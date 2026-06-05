package com.example.apptravelfood.domain.model

data class Place(
    val placeId: String,
    val title: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val thumbnail: String?
)