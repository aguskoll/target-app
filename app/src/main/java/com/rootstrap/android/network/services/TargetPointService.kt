package com.rootstrap.android.network.services

import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.network.models.TopicsSerializer
import com.rootstrap.android.network.providers.ServiceProvider
import com.rootstrap.android.util.extensions.ActionCallback
import com.rootstrap.android.util.extensions.Data

object TargetPointService : ITargetPointService {

    private var service = ServiceProvider.create(ApiService::class.java)

    override suspend fun createTarget(target: Target): Result<Data<TargetPointSerializer>> =
        ActionCallback.call(service.createTarget(TargetPointSerializer(target)))

    override suspend fun getTopics(): Result<Data<TopicsSerializer>> =
        ActionCallback.call(service.getTopics())
}
