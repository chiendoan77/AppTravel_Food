package com.example.apptravelfood.data.remote.api

import com.example.apptravelfood.data.remote.dto.GoogleMapsResponseDto
import com.example.apptravelfood.data.remote.dto.SerpResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SepriAPI {

    @GET("search.json")
    suspend fun searchPlaces(
        @Query("engine") engine: String = "google_local",
        @Query("q") query: String,
        @Query("location") location: String,
        @Query("google_domain") domain: String = "google.com.vn",
        @Query("hl") hl: String = "vi",
        @Query("gl") gl: String = "vn",
        @Query("api_key") apiKey: String
    ): SerpResponseDto

    @GET("search.json")
    suspend fun searchAddress(
        @Query("engine") engine: String = "google_maps",
        @Query("q") query: String,
        @Query("ll") ll: String? = null,
        @Query("type") type: String = "search",
        @Query("hl") hl: String = "vi",
        @Query("gl") gl: String = "vn",
        @Query("api_key") apiKey: String
    ): GoogleMapsResponseDto
}