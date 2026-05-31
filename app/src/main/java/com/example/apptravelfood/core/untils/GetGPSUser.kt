package com.example.apptravelfood.core.untils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double,
    onResult: (String?, String?, String?) -> Unit
) {

    val geocoder = Geocoder(
        context,
        Locale.forLanguageTag("vi-VN")
    )

    geocoder.getFromLocation(
        latitude,
        longitude,
        1,
        object : Geocoder.GeocodeListener {

            override fun onGeocode(
                addresses: MutableList<Address>
            ) {

                val address = addresses.firstOrNull()

                val city =
                    address?.locality
                        ?: address?.subAdminArea
                        ?: address?.subLocality
                        ?: address?.featureName
                val province = address?.adminArea
                val country = address?.countryName

                onResult(
                    city,
                    province,
                    country
                )
            }

            override fun onError(
                errorMessage: String?
            ) {
                onResult(
                    null,
                    null,
                    null
                )
            }
        }
    )
}