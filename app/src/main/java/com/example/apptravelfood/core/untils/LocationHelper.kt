package com.example.apptravelfood.core.untils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation(
        onResult: (Double, Double) -> Unit
    ) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        )
            .setMaxUpdates(1)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation

                if (location != null) {
                    Log.d(
                        "LOCATION",
                        "FreshLocation Lat=${location.latitude}, Lng=${location.longitude}"
                    )

                    onResult(
                        location.latitude,
                        location.longitude
                    )
                } else {
                    Log.d("LOCATION", "FreshLocation null")
                }

                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }
}