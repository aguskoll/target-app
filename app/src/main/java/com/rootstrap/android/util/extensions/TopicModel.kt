package com.rootstrap.android.util.extensions

import com.rootstrap.android.R
import com.rootstrap.android.models.TopicModel
import com.rootstrap.android.models.TopicTypes

// todo replace chess and mate icon for the corresponding icon
fun TopicModel.getIconForTarget(): Int {
    return when (label) {
        TopicTypes.football -> R.drawable.ic_ball
        TopicTypes.travel -> R.drawable.ic_world
        TopicTypes.politics -> R.drawable.ic_politics
        TopicTypes.art -> R.drawable.ic_art
        TopicTypes.dating -> R.drawable.ic_dating
        TopicTypes.music -> R.drawable.ic_music
        TopicTypes.movies -> R.drawable.ic_movies
        TopicTypes.series -> R.drawable.ic_series
        TopicTypes.food -> R.drawable.ic_food
        TopicTypes.chess -> R.drawable.ic_art
        TopicTypes.mate -> R.drawable.ic_food
    }
}
