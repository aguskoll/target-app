package com.rootstrap.android.models

import com.rootstrap.android.network.models.Target

data class TargetModel(
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: Double = 0.0,
    val topic: TopicModel? = null,
    val id: Long = 0
)

fun TargetModel.mapToTargetRequest(): Target {
    return Target(
        title = title,
        lat = lat,
        lng = lng,
        radius = radius,
        topic_id = topic?.id ?: 0,
        id = id
    )
}
