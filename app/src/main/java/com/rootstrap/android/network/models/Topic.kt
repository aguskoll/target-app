package com.rootstrap.android.network.models

import com.squareup.moshi.Json

@Json(name = "topic")
data class Topic(
    @Json(name = "id") val id: Int,
    @Json(name = "label") val label: String = "",
    @Json(name = "icon") val icon: String = ""
)

data class TopicsSerializer(@Json(name = "topics") val topics: List<Topic>)
