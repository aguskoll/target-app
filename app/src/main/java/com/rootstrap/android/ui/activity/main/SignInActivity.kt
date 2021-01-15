package com.rootstrap.android.ui.activity.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivitySignInBinding
import com.rootstrap.android.metrics.Analytics
import com.rootstrap.android.metrics.PageEvents
import com.rootstrap.android.metrics.VISIT_SIGN_IN
import com.rootstrap.android.network.models.User
import com.rootstrap.android.ui.view.AuthView
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.util.extensions.value
import com.rootstrap.android.util.permissions.PermissionActivity
import com.rootstrap.android.util.permissions.PermissionResponse

class SignInActivity : PermissionActivity(), AuthView {

    private lateinit var viewModel: SignInActivityViewModel

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Analytics.track(PageEvents.visit(VISIT_SIGN_IN))

        val factory = SignInActivityViewModelFactory(viewModelListener)
        viewModel = ViewModelProvider(this, factory)
            .get(SignInActivityViewModel::class.java)

        binding.signInButton.setOnClickListener { signIn() }
        binding.signUpTextView.setOnClickListener { goToSignUp() }

        lifecycle.addObserver(viewModel)

        sampleAskForPermission()
    }

    override fun showProfile() {
        startActivityClearTask(ProfileActivity())
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

    // ViewModelListener
    private val viewModelListener = object : ViewModelListener {
        override fun updateState() {
            when (viewModel.state) {
                SignInState.signInFailure, SignInState.none -> showLoginError()
                SignInState.signInSuccess -> showProfile()
            }
        }

        override fun updateNetworkState() {
            when (viewModel.networkState) {
                NetworkState.loading -> showProgress()
                NetworkState.idle -> hideProgress()
                else -> {
                    hideProgress()
                    if (viewModel.error.isNullOrEmpty())
                        showError(getString(R.string.default_error))
                }
            }
        }
    }

    private fun sampleAskForPermission() {
        requestPermission(arrayOf(Manifest.permission.CAMERA), object : PermissionResponse {
            override fun granted() = Unit

            override fun denied() = Unit

            override fun foreverDenied() = Unit
        })
    }
}
