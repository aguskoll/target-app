package com.rootstrap.android.network.managers

import com.rootstrap.android.network.models.TargetPoint
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.network.providers.ServiceProvider
import com.rootstrap.android.network.services.ApiService
import com.rootstrap.android.util.extensions.ActionCallback
import com.rootstrap.android.util.extensions.Data

object TargetPointManager : ITargetPointManager {

    private var service = ServiceProvider.create(ApiService::class.java)

    override suspend fun createTarget(targetPoint: TargetPoint): Result<Data<TargetPointSerializer>> =
        ActionCallback.call(service.createTarget(TargetPointSerializer(targetPoint)))

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
