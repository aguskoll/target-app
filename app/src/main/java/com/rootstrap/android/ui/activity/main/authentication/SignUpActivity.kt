package com.rootstrap.android.ui.activity.main.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.rootstrap.android.R
import com.rootstrap.android.metrics.Analytics
import com.rootstrap.android.metrics.PageEvents
import com.rootstrap.android.metrics.VISIT_SIGN_UP
import com.rootstrap.android.network.models.User
import com.rootstrap.android.ui.activity.main.targetpoint.TargetPointsActivity
import com.rootstrap.android.ui.base.BaseActivity
import com.rootstrap.android.ui.view.AuthView
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.util.extensions.value
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(), AuthView {

    private lateinit var viewModel: SignUpActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Analytics.track(PageEvents.visit(VISIT_SIGN_UP))

        val factory = SignUpActivityViewModelFactory(viewModelListener)

        viewModel = ViewModelProvider(this, factory)
            .get(SignUpActivityViewModel::class.java)

        sign_up_button.setOnClickListener { signUp() }
        sign_in_text_view.setOnClickListener {
            goToLogin()
        }

        initGenderDropDown()
        checkTextInput()
        lifecycle.addObserver(viewModel)
    }

    private fun checkTextInput() {
        addTextChangeListener(first_name_edit_text) { checkErrorForName() }
        addTextChangeListener(email_edit_text) { checkErrorForEmail() }
        addTextChangeListener(password_edit_text) {
            val validPass = checkErrorForPassword()
            if (confirm_password_edit_text.value().isNotEmpty())
                checkErrorForConfirmPassword()
            validPass
        }
        addTextChangeListener(confirm_password_edit_text) { checkErrorForConfirmPassword() }
    }

    private fun addTextChangeListener(editText: EditText, checkValidation: () -> Boolean) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                checkValidation()
            }
        })
    }

    private fun checkErrorForName(): Boolean {
        val isUserNameValid = viewModel.isUserNameValid(first_name_edit_text.value())
        first_name_text_input_layout.error =
            if (isUserNameValid.not())
                getString(R.string.error_input_no_name)
            else null
        return isUserNameValid
    }

    private fun checkErrorForEmail(): Boolean {
        val isEmailValid = viewModel.isEmailValid(email_edit_text.value())
        email_text_input_layout.error = if (isEmailValid.not())
            getString(R.string.error_invalid_email)
        else null
        return isEmailValid
    }

    private fun checkErrorForPassword(): Boolean {
        val isPassValid = viewModel.isPasswordValid(password_edit_text.value())
        password_text_input_layout.error =
            if (isPassValid.not()) {
                getString(R.string.error_password_length)
            } else null
        return isPassValid
    }

    private fun checkErrorForConfirmPassword(): Boolean {
        val isConfirmPassValid = viewModel.isConfirmPasswordValid(
            password_edit_text.value(),
            confirm_password_edit_text.value()
        )
        confirm_password_text_input_layout.error = if (isConfirmPassValid.not()) {
            getString(R.string.error_passwords_not_match)
        } else null

        return isConfirmPassValid
    }

    private fun checkErrorGender(): Boolean {
        val isGenderValid = viewModel.isGenderValid(gender_drop_down_text.value())
        gender_drop_down.error = if (isGenderValid.not()) {
            getString(R.string.error_forgot_select_gender)
        } else null
        return isGenderValid
    }

    private fun goToLogin() {
        startActivity(
            Intent(
                this,
                SignInActivity::class.java
            )
        )
    }

    override fun showMainPage() {
        startActivityClearTask(TargetPointsActivity())
    }

    private fun initGenderDropDown() {
        val items =
            listOf(getString(R.string.female), getString(R.string.male), getString(R.string.other))
        val adapter = ArrayAdapter(this, R.layout.gender_list_item, items)
        (gender_drop_down_text)?.setAdapter(adapter)
        gender_drop_down_text.isAllCaps = true

        addTextChangeListener(gender_drop_down_text) { checkErrorGender() }
    }

    private fun isUserInputValid(): Boolean {
        return checkErrorForName().and(
            checkErrorForEmail()
        ).and(
            checkErrorForPassword()
        ).and(
            checkErrorForConfirmPassword()
        ).and(
            checkErrorGender()
        )
    }

    private fun signUp() {
        if (isUserInputValid()) {
            val user = User(
                email = email_edit_text.value(),
                username = first_name_edit_text.value(),
                passwordConfirmation = confirm_password_edit_text.value(),
                password = password_edit_text.value(),
                gender = gender_drop_down_text.value().toLowerCase()
            )
            viewModel.signUp(user)
        }
    }

    // ViewModelListener
    private val viewModelListener = object : ViewModelListener {
        override fun updateState() {
            when (viewModel.state) {
                SignUpState.signUpSuccess -> showMainPage()
                SignUpState.none, SignUpState.signUpFailure -> Unit
            }
        }

        override fun updateNetworkState() {
            when (viewModel.networkState) {
                NetworkState.loading -> showProgress()
                NetworkState.idle -> hideProgress()
                NetworkState.error -> {
                    hideProgress()
                    showError(viewModel.error ?: getString(R.string.default_error))
                }
            }
        }
    }
}
