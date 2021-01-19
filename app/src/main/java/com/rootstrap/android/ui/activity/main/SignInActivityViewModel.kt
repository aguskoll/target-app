package com.rootstrap.android.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rootstrap.android.network.managers.IUserManager
import com.rootstrap.android.network.managers.SessionManager
import com.rootstrap.android.network.managers.UserManager
import com.rootstrap.android.network.models.FacebookSignIn
import com.rootstrap.android.network.models.User
import com.rootstrap.android.network.models.UserSerializer
import com.rootstrap.android.ui.activity.main.SignUpActivityViewModel.Companion.MIN_CHAR_PASSWORD
import com.rootstrap.android.ui.base.BaseViewModel
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.util.extensions.ApiErrorType
import com.rootstrap.android.util.extensions.ApiException
import com.rootstrap.android.util.extensions.Data
import kotlinx.coroutines.launch
import java.io.IOException

open class SignInActivityViewModel(
    listener: ViewModelListener?,
    private var manager: IUserManager
) : BaseViewModel(listener) {

    var state: SignInState = SignInState.none
        set(value) {
            field = value
            listener?.updateState()
        }

    fun canSignIn(userName: String?, password: String?): Boolean {
        return userName.isNullOrEmpty().not() &&
                password != null &&
                password.isNotEmpty() &&
                password.length >= MIN_CHAR_PASSWORD
    }

    fun signIn(user: User) {
        networkState = NetworkState.loading
        viewModelScope.launch {
            try {
                val result = manager.signIn(user = user)
                signInResult(result)
            } catch (exception: IOException) {
                handleError(Throwable())
            }
        }
    }

    fun signInWithFacebook(token: String) {
        networkState = NetworkState.loading
        SessionManager.addFacebookToken(token)
        viewModelScope.launch {
            try {
                val result = manager.signInWithFacebook(FacebookSignIn(token))
                signInResult(result)
            } catch (exception: IOException) {
                handleError(Throwable())
            }
        }
    }

    private fun signInResult(result: Result<Data<UserSerializer>>) {
        if (result.isSuccess) {
            result.getOrNull()?.value?.user?.let { user ->
                SessionManager.signIn(user)
            }
            networkState = NetworkState.idle
            state = SignInState.signInSuccess
        } else {
            handleError(result.exceptionOrNull())
        }
    }

    private fun handleError(exception: Throwable?) {
        error = if (exception is ApiException && exception.errorType == ApiErrorType.apiError) {
            exception.message
        } else null

        networkState = NetworkState.idle
        networkState = NetworkState.error
        state = SignInState.signInFailure
    }
}

enum class SignInState {
    signInFailure,
    signInSuccess,
    none,
}

class SignInActivityViewModelFactory(var listener: ViewModelListener?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignInActivityViewModel(listener, UserManager) as T
    }
}
