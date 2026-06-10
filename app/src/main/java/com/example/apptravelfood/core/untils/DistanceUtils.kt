package com.example.apptravelfood.core.untils

import android.location.Location

object DistanceUtils {

    fun calculateDistanceKm(
        startLat: Double?,
        startLng: Double?,
        endLat: Double?,
        endLng: Double?
    ): Double? {
        if (
            startLat == null ||
            startLng == null ||
            endLat == null ||
            endLng == null
        ) return null

        val result = FloatArray(1)

        Location.distanceBetween(
            startLat,
            startLng,
            endLat,
            endLng,
            result
        )

        return result[0] / 1000.0
    }

    fun formatDistance(distanceKm: Double?): String {
        if (distanceKm == null) return "Chưa rõ khoảng cách"

        return if (distanceKm < 1) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            String.format("%.1f km", distanceKm)
        }
    }
}