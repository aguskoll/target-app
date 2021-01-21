package com.rootstrap.android.ui.activity.main.authentication

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivitySignInBinding
import com.rootstrap.android.metrics.Analytics
import com.rootstrap.android.metrics.PageEvents
import com.rootstrap.android.metrics.VISIT_SIGN_IN
import com.rootstrap.android.network.models.User
import com.rootstrap.android.ui.activity.main.targetpoint.TargetPointsActivity
import com.rootstrap.android.ui.view.AuthView
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.util.extensions.value
import com.rootstrap.android.util.permissions.PermissionActivity

class SignInActivity : PermissionActivity(), AuthView {

    private lateinit var viewModel: SignInActivityViewModel

    private lateinit var binding: ActivitySignInBinding

    private lateinit var faceBookCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Analytics.track(PageEvents.visit(VISIT_SIGN_IN))

        val factory = SignInActivityViewModelFactory(viewModelListener)

        viewModel = ViewModelProvider(this, factory)
            .get(SignInActivityViewModel::class.java)

        logInCallbackFacebook()

        initView()

        lifecycle.addObserver(viewModel)
    }

    private fun initView() {
        binding.signInButton.setOnClickListener { signIn() }
        binding.signUpTextView.setOnClickListener { goToSignUp() }
        binding.connectWithFacebookTextView.setOnClickListener { logInWithFacebook() }
    }

    override fun showMainPage() {
        startActivityClearTask(TargetPointsActivity())
    }

    private fun logInWithFacebook() {
        LoginManager.getInstance().logIn(this, arrayListOf(FACEBOOK_PERMISSION))
    }

    private fun logInCallbackFacebook() {
        faceBookCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            faceBookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) = Unit

                override fun onCancel() = Unit

                override fun onError(error: FacebookException?) {
                    showError(null)
                }
            })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        faceBookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        val accessToken = AccessToken.getCurrentAccessToken()
        accessToken?.token?.run {
            viewModel.signInWithFacebook(this)
        }
    }

    private fun goToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun signIn() {
        val name = binding.emailEditText.value()
        val pass = binding.passwordEditText.value()

        if (viewModel.canSignIn(name, pass)) {
            val user = User(
                email = name,
                password = pass
            )
            viewModel.signIn(user)
        } else {
            showLoginError()
        }
    }

    private fun showLoginError() {
        binding.passwordTextInputLayout.error = getString(R.string.login_failed)
        binding.emailTextInputLayout.error = " "
    }

    private val viewModelListener = object : ViewModelListener {
        override fun updateState() {
            when (viewModel.state) {
                SignInState.signInFailure, SignInState.none -> Unit
                SignInState.signInSuccess -> showMainPage()
            }
        }

        override fun updateNetworkState() {
            when (viewModel.networkState) {
                NetworkState.loading -> showProgress()
                NetworkState.idle -> hideProgress()
                NetworkState.error -> {
                    hideProgress()
                    if (viewModel.error.isNullOrEmpty())
                        showError(getString(R.string.default_error))
                    else showLoginError()
                }
            }
        }
    }

    companion object {
        const val FACEBOOK_PERMISSION = "public_profile"
    }
}
