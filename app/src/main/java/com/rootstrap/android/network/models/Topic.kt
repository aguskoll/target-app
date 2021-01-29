package com.rootstrap.android.network.models

import com.rootstrap.android.models.TopicModel
import com.squareup.moshi.Json

data class Topic(
    @Json(name = "id") val id: Int,
    @Json(name = "label") val label: String = "",
    @Json(name = "icon") val icon: String = ""
)

data class TopicsSerializer(@Json(name = "topics") val topics: List<TopicSerializer>)

data class TopicSerializer(@Json(name = "topic") val topic: Topic)

fun Topic.mapToModel(): TopicModel {
    return TopicModel(
        id = id,
        label = label,
        icon = icon
    )
}
