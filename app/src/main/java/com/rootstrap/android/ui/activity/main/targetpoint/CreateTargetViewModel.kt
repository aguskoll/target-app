package com.rootstrap.android.ui.activity.main.targetpoint

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rootstrap.android.network.managers.ITargetPointManager
import com.rootstrap.android.network.managers.TargetPointManager
import com.rootstrap.android.network.models.TargetPoint
import com.rootstrap.android.ui.base.BaseViewModel
import com.rootstrap.android.util.NetworkState
import kotlinx.coroutines.launch
import java.io.IOException

class CreateTargetViewModel(private val targetManager: ITargetPointManager) : BaseViewModel(null) {

    var createTargetState: MutableLiveData<CreateTargetState> = MutableLiveData()

    fun createTarget(targetPoint: TargetPoint) {
        try {
            networkState = NetworkState.loading
            viewModelScope.launch {
                val result = targetManager.createTarget(targetPoint)
                if (result.isSuccess) {
                    createTargetState.value = CreateTargetState.success
                } else {
                    createTargetState.value = CreateTargetState.fail
                }
            }
        } catch (exception: IOException) {
            networkState = NetworkState.error
            exception.printStackTrace()
        }
    }

    fun saveUserLocation(lat: Double, lng: Double) {
        targetManager.saveUserLocation(lat, lng)
    }

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
