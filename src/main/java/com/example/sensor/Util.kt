package com.example.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*

object Util {
    @SuppressLint("MissingPermission")
    fun getAddress(context: Context, getAddress:GetAddress) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(context)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        locationResult.locations.size - 1
                        val geoCoder = Geocoder(context, Locale.getDefault())
                        val addresses: List<Address> = geoCoder.getFromLocation(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude,
                            1
                        )
                        val value = addresses[0].getAddressLine(0)
                        val addressLineOne = addresses[0].thoroughfare
                        val addressLineTwo = addresses[0].subThoroughfare
                        val pinCode = addresses[0].postalCode
                        val countryName = addresses[0].countryName
                        val city = addresses[0].subLocality
                        val state = addresses[0].adminArea
                        val district = addresses[0].locality
                        val doorNumber = addresses[0].featureName
                        val line2 = addresses[0].phone
                        val line3 =addresses[0].premises

                        getAddress.getAddress(value,doorNumber,addressLineOne,addressLineTwo,city,district,state,pinCode,countryName)
                    }
                }
            }, Looper.getMainLooper())
    }

    fun setAddressInParticularFormat(value: String):String{
        var addressFormat = ""
        val result = value.split(", ")
        for (i in result.indices) {
            addressFormat = if (i == 0)
                result[i]
            else
                addressFormat + ", \n" + result[i]
        }
        return addressFormat
    }

    interface GetAddress{
        fun getAddress(fullAddress:String?, doorNumber:String?,addressLineOne:String?,addressLineTwo:String?, city:String?, district:String?, state:String?, pinCode:String?, countryName:String?)
    }
}