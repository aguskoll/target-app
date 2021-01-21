package com.rootstrap.android.network.models

import com.squareup.moshi.Json

data class TargetPoint(
    @Json(name = "title") val title: String = "",
    @Json(name = "lat") val lat: Double = 0.0,
    @Json(name = "lng") val lng: Double = 0.0,
    @Json(name = "radius") val radius: Double = 0.0,
    @Json(name = "topic_id") val topic_id: Int = 0
)

data class TargetPointSerializer(@Json(name = "target") val targetPoint: TargetPoint)
