package com.rootstrap.android.ui.activity.main.targetpoint

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rootstrap.android.network.managers.ITargetPointManager
import com.rootstrap.android.network.managers.TargetPointManager
import com.rootstrap.android.network.models.Target
import com.rootstrap.android.network.models.Topic
import com.rootstrap.android.ui.base.BaseViewModel
import com.rootstrap.android.util.NetworkState
import kotlinx.coroutines.launch
import java.io.IOException

class CreateTargetViewModel(private val targetManager: ITargetPointManager) : BaseViewModel(null) {

    var createTargetState: MutableLiveData<CreateTargetState> = MutableLiveData()
    var newTarget: MutableLiveData<Target> = MutableLiveData()

    fun createTarget(target: Target) {
        try {
            networkState = NetworkState.loading
            viewModelScope.launch {
                val result = targetManager.createTarget(target)
                if (result.isSuccess) {
                    handleSuccess(result.getOrNull()?.value?.target)
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    fun getTopics() {
        try {
            viewModelScope.launch {
                val result = targetManager.getTopics()
                if (result.isSuccess) {
                    val topics: List<Topic> = result.getOrNull()?.value?.topics ?: emptyList()
                } else {
                    handleError(result.exceptionOrNull())
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    private fun handleSuccess(target: Target?) {
        createTargetState.postValue(CreateTargetState.success)
        networkState = NetworkState.idle
        target?.let {
            newTarget.postValue(it)
        }
    }

    private fun handleError(exception: Throwable?) {
        createTargetState.value = CreateTargetState.fail
        networkState = NetworkState.error
        error = getMessageErrorFromException(exception)
    }

    fun saveUserLocation(lat: Double, lng: Double) = targetManager.saveUserLocation(lat, lng)

    fun getLocationLatitude(): Double = targetManager.getLocationLatitude()

    fun getLocationLongitude(): Double = targetManager.getLocationLongitude()

    fun isLocationStateSuccess(): Boolean = targetManager.isLocationStateSuccess()
}

class CreateTargetViewModelViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateTargetViewModel(TargetPointManager) as T
    }
}

enum class CreateTargetState {
    fail,
    success,
    none
}
