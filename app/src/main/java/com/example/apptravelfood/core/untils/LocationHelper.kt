package com.example.apptravelfood.core.untils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices

class LocationHelper(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onResult: (Double, Double) -> Unit
    ) {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LOCATION", "Lat=${location.latitude}, Lng=${location.longitude}")
                    onResult(location.latitude, location.longitude)
                } else {
                    Log.d("LOCATION", "Location null")
                }
            }
            .addOnFailureListener {
                Log.e("LOCATION", "Lỗi lấy vị trí: ${it.message}")
            }
    }
}