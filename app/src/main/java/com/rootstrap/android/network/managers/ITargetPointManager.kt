package com.rootstrap.android.network.managers

import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.network.models.TopicsSerializer
import com.rootstrap.android.util.extensions.Data

interface ITargetPointManager {

    suspend fun createTarget(target: Target): Result<Data<TargetPointSerializer>>

    suspend fun getTopics(): Result<Data<TopicsSerializer>>
}
