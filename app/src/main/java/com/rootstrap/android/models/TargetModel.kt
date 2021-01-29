package com.rootstrap.android.models

data class TargetModel(
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: Double = 0.0,
    val topic: TopicModel? = null
)
