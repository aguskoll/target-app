package com.rootstrap.android.network.managers

import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.util.extensions.Data

interface ITargetPointManager {
    suspend fun createTarget(target: Target): Result<Data<TargetPointSerializer>>

    fun saveUserLocation(lat: Double, lng: Double)

    fun isLocationStateSuccess(): Boolean

    fun getLocationLatitude(): Double

    fun getLocationLongitude(): Double
}
