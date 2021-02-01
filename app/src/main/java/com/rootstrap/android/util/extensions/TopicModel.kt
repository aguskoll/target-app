package com.rootstrap.android.util.extensions

import com.rootstrap.android.R
import com.rootstrap.android.models.TopicModel

fun TopicModel.getIconForTarget(): Int {
    return when (label?.toLowerCase()) {
        FOOTBALL -> R.drawable.ic_ball
        TRAVEL -> R.drawable.ic_world
        POLITICS -> R.drawable.ic_politics
        ART -> R.drawable.ic_art
        DATING -> R.drawable.ic_dating
        MUSIC -> R.drawable.ic_music
        MOVIES -> R.drawable.ic_movies
        SERIES -> R.drawable.ic_series
        else -> R.drawable.ic_food
    }
}

const val FOOTBALL = "football"
const val TRAVEL = "travel"
const val POLITICS = "politics"
const val ART = "art"
const val DATING = "dating"
const val MUSIC = "music"
const val MOVIES = "movies"
const val SERIES = "series"
