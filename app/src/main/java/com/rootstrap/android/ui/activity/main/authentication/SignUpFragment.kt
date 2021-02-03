package com.rootstrap.android.ui.activity.main.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.rootstrap.android.R
import com.rootstrap.android.databinding.FragmentSignUpBinding
import com.rootstrap.android.network.models.User
import com.rootstrap.android.ui.base.BaseFragment
import com.rootstrap.android.ui.view.AuthView
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.util.extensions.value

class SignUpFragment : BaseFragment(), AuthView {

    private lateinit var viewModel: SignUpActivityViewModel
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var bindingRoot: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        bindingRoot = binding.root

        val factory = SignUpActivityViewModelFactory(viewModelListener)

        viewModel = ViewModelProvider(this, factory)
            .get(SignUpActivityViewModel::class.java)

        lifecycle.addObserver(viewModel)

        return bindingRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            signUpButton.setOnClickListener { signUp() }

            signInTextView.setOnClickListener {
                goToLogin()
            }
        }

        initGenderDropDown()
        checkTextInput()
    }

    private fun goToLogin() {
        bindingRoot.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }

    override fun showMainPage() {
        bindingRoot.findNavController().navigate(R.id.action_signUpFragment_to_targetPointsActivity)
    }

    private fun checkTextInput() {
        with(binding) {
            addTextChangeListener(firstNameEditText) { checkErrorForName() }
            addTextChangeListener(emailEditText) { checkErrorForEmail() }
            addTextChangeListener(passwordEditText) {
                val validPass = checkErrorForPassword()
                if (confirmPasswordEditText.value().isNotEmpty())
                    checkErrorForConfirmPassword()
                validPass
            }
            addTextChangeListener(confirmPasswordEditText) { checkErrorForConfirmPassword() }
        }
    }

    private fun addTextChangeListener(editText: EditText, checkValidation: () -> Boolean) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                checkValidation()
            }
        })
    }

    private fun checkErrorForName(): Boolean {
        with(binding) {
            val isUserNameValid = viewModel.isUserNameValid(firstNameEditText.value())
            firstNameTextInputLayout.error =
                if (isUserNameValid.not())
                    getString(R.string.error_input_no_name)
                else null
            return isUserNameValid
        }
    }

    private fun checkErrorForEmail(): Boolean {
        with(binding) {
            val isEmailValid = viewModel.isEmailValid(emailEditText.value())
            emailTextInputLayout.error = if (isEmailValid.not())
                getString(R.string.error_invalid_email)
            else null
            return isEmailValid
        }
    }

    private fun checkErrorForPassword(): Boolean {
        with(binding) {
            val isPassValid = viewModel.isPasswordValid(passwordEditText.value())
            passwordTextInputLayout.error =
                if (isPassValid.not()) {
                    getString(R.string.error_password_length)
                } else null
            return isPassValid
        }
    }

    private fun checkErrorForConfirmPassword(): Boolean {
        with(binding) {
            val isConfirmPassValid = viewModel.isConfirmPasswordValid(
                passwordEditText.value(),
                confirmPasswordEditText.value()
            )
            confirmPasswordTextInputLayout.error = if (isConfirmPassValid.not()) {
                getString(R.string.error_passwords_not_match)
            } else null

            return isConfirmPassValid
        }
    }

    private fun checkErrorGender(): Boolean {
        with(binding) {
            val isGenderValid = viewModel.isGenderValid(genderDropDownText.value())
            genderDropDown.error = if (isGenderValid.not()) {
                getString(R.string.error_forgot_select_gender)
            } else null
            return isGenderValid
        }
    }

    private fun initGenderDropDown() {
        with(binding) {
            val items = listOf(getString(R.string.female), getString(R.string.male), getString(R.string.other))
            val adapter = ArrayAdapter(requireContext(), R.layout.gender_list_item, items)
            (genderDropDownText).setAdapter(adapter)
            genderDropDownText.isAllCaps = true

            addTextChangeListener(binding.genderDropDownText) { checkErrorGender() }
        }
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
            with(binding) {
                val user = User(
                    email = emailEditText.value(),
                    username = firstNameEditText.value(),
                    passwordConfirmation = confirmPasswordEditText.value(),
                    password = passwordEditText.value(),
                    gender = genderDropDownText.value().toLowerCase()
                )
                viewModel.signUp(user)
            }
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
