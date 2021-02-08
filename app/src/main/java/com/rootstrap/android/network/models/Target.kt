package com.rootstrap.android.network.models

import com.rootstrap.android.models.TargetModel
import com.rootstrap.android.models.TopicModel
import com.squareup.moshi.Json

data class Target(
    @Json(name = "title") val title: String = "",
    @Json(name = "lat") val lat: Double = 0.0,
    @Json(name = "lng") val lng: Double = 0.0,
    @Json(name = "radius") val radius: Double = 0.0,
    @Json(name = "topic_id") val topic_id: Int = 0,
    @Json(name = "id") val id: Long = 0
)

data class TargetPointSerializer(@Json(name = "target") val target: Target)

data class TargetsSerializer(@Json(name = "targets") val targets: List<TargetPointSerializer>)

fun Target.mapToModel(topic: TopicModel?): TargetModel {
    return TargetModel(
        title = title,
        lat = lat,
        lng = lng,
        radius = radius,
        topic = topic,
        id = id
    )
}
