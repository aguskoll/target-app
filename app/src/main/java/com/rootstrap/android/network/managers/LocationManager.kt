package com.rootstrap.android.network.managers

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationManager : ILocationManager {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun getDeviceLocation(context: Context, successActionCallback: (location: Location) -> Unit) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
        try {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                location?.run {
                    saveUserLocation(longitude, latitude)
                    successActionCallback(location)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun saveUserLocation(lat: Double, lng: Double) {
        UserLocation.latitude = lat
        UserLocation.longitude = lng
        UserLocation.locationState = LocationState.success
    }

    override fun isLocationStateSuccess(): Boolean {
        return UserLocation.locationState == LocationState.success
    }

    override fun getLocationLatitude(): Double = UserLocation.latitude

    override fun getLocationLongitude(): Double = UserLocation.longitude
}

object UserLocation {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var locationState: LocationState = LocationState.none
}

enum class LocationState {
    fail,
    success,
    none
}
