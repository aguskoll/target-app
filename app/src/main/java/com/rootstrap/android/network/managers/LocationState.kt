package com.rootstrap.android.network.managers

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
