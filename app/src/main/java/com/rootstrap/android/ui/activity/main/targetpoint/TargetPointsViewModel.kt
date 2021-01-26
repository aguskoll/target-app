package com.rootstrap.android.ui.activity.main.targetpoint

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rootstrap.android.network.managers.ILocationManager
import com.rootstrap.android.network.managers.LocationManager
import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.Topic
import com.rootstrap.android.network.models.TopicSerializer
import com.rootstrap.android.network.services.ITargetPointService
import com.rootstrap.android.network.services.TargetPointService
import com.rootstrap.android.ui.base.BaseViewModel
import com.rootstrap.android.util.NetworkState
import kotlinx.coroutines.launch
import java.io.IOException

class TargetPointsViewModel(
    private val targetService: ITargetPointService,
    private val locationManager: ILocationManager
) : BaseViewModel(null) {

    var createTargetState: MutableLiveData<CreateTargetState> = MutableLiveData()
    var newTarget: MutableLiveData<Target> = MutableLiveData()
    var networkStateObservable: MutableLiveData<NetworkState> = MutableLiveData()
    var topics: MutableLiveData<List<Topic>> = MutableLiveData()

    fun createTarget(target: Target) {
        try {
            networkStateObservable.postValue(NetworkState.loading)
            viewModelScope.launch {
                val result = targetService.createTarget(target)
                if (result.isSuccess) {
                    handleSuccess(result.getOrNull()?.value?.target)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
        } catch (exception: IOException) {
            handleError(exception)
            exception.printStackTrace()
        }
    }

    fun getDeviceLocation(context: Context, successAction: (location: Location) -> Unit) {
        locationManager.getDeviceLocation(context, successAction)
    }

    fun getTopics(): LiveData<List<Topic>> {
        try {
            viewModelScope.launch {
                val result = targetService.getTopics()
                if (result.isSuccess) {
                    val topicsSerializer: List<TopicSerializer> = result.getOrNull()?.value?.topics ?: emptyList()
                    topics.postValue(topicsSerializer.map { it.topic })
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        return topics
    }

    private fun handleSuccess(target: Target?) {
        createTargetState.postValue(CreateTargetState.success)
        networkStateObservable.postValue(NetworkState.idle)
        target?.let {
            newTarget.postValue(it)
        }
    }

    private fun handleError(exception: Throwable?) {
        createTargetState.postValue(CreateTargetState.fail)
        networkStateObservable.postValue(NetworkState.error)
        error = getMessageErrorFromException(exception)
    }

    fun getLocationLatitude(): Double = locationManager.getLocationLatitude()

    fun getLocationLongitude(): Double = locationManager.getLocationLongitude()

    fun isLocationStateSuccess(): Boolean = locationManager.isLocationStateSuccess()

    fun isAreaValid(area: Double): Boolean = area > 0

    fun isTitleValid(title: String?): Boolean = title.isNullOrEmpty().not()

    fun isTopicValid(topic: Int): Boolean = topic > 0
}

class CreateTargetViewModelViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TargetPointsViewModel(TargetPointService, LocationManager) as T
    }
}

enum class CreateTargetState {
    fail,
    success,
    none
}