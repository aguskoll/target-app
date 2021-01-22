package com.rootstrap.android.network.managers

import android.content.Context
import android.location.Location

interface ILocationManager {
    fun saveUserLocation(lat: Double, lng: Double)

    fun isLocationStateSuccess(): Boolean

    fun getLocationLatitude(): Double

    fun getLocationLongitude(): Double

    fun getDeviceLocation(context: Context, successAction: (location: Location) -> Unit)
}
