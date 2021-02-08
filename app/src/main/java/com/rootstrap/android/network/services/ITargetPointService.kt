package com.rootstrap.android.network.services

import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.network.models.TargetsSerializer
import com.rootstrap.android.network.models.TopicsSerializer
import com.rootstrap.android.util.extensions.Data

interface ITargetPointService {

    suspend fun createTarget(target: Target): Result<Data<TargetPointSerializer>>

    suspend fun getTopics(): Result<Data<TopicsSerializer>>

    suspend fun getTargets(): Result<Data<TargetsSerializer>>

    suspend fun deleteTarget(id: Long): Result<Data<Void>>
}
