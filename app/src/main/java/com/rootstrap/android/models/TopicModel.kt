package com.rootstrap.android.models

data class TopicModel(
    val id: Int,
    val label: TopicTypes,
    val icon: String = ""
)

enum class TopicTypes {
    chess,
    football,
    travel,
    politics,
    art,
    dating,
    music,
    movies,
    series,
    food,
    mate
}
