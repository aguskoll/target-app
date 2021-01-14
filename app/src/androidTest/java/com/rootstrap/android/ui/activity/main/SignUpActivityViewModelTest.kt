package com.rootstrap.android.ui.activity.main

import com.rootstrap.android.network.models.User
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.ViewModelListener
import junit.framework.Assert
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.MockitoAnnotations

class SignUpActivityViewModelTest : TestCase() {

    lateinit var signUpActivityViewModel: SignUpActivityViewModel

    public override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)
        signUpActivityViewModel = SignUpActivityViewModel(object : ViewModelListener {
            override fun updateState() {}

            override fun updateNetworkState() {}
        })
    }

    @Test
    fun testSetState() {
        signUpActivityViewModel.state = SignUpState.signUpSuccess
        assert(signUpActivityViewModel.state == SignUpState.signUpSuccess)
    }

    @Test
    fun testIsUserNameValid() {
        assert(signUpActivityViewModel.isUserNameValid(VALID_NAME))
    }

    @Test
    fun testIsUserNameNotValid() {
        Assert.assertFalse(signUpActivityViewModel.isUserNameValid(INVALID_NAME))
    }

    @Test
    fun testIsUserNameValidWhenNull() {
        Assert.assertFalse(signUpActivityViewModel.isUserNameValid(null))
    }

    @Test
    fun testIsEmailValid() {
        assert(signUpActivityViewModel.isEmailValid(VALID_MAIL))
    }

    @Test
    fun testIsEmailNotValid() {
        Assert.assertFalse(signUpActivityViewModel.isEmailValid(INVALID_MAIL))
    }

    @Test
    fun testIsEmailValidWhenNull() {
        Assert.assertFalse(signUpActivityViewModel.isEmailValid(INVALID_MAIL))
    }

    @Test
    fun testIsPasswordValid() {
        assert(signUpActivityViewModel.isPasswordValid(VALID_PASSWORD))
    }

    @Test
    fun testIsPasswordNotValid() {
        Assert.assertFalse(signUpActivityViewModel.isPasswordValid(INVALID_PASSWORD))
    }

    @Test
    fun testIsPasswordValidWhenIsNull() {
        Assert.assertFalse(signUpActivityViewModel.isPasswordValid(null))
    }

    @Test
    fun testIsConfirmPasswordValid() {
        assert(
            signUpActivityViewModel.isConfirmPasswordValid(
                VALID_PASSWORD,
                VALID_CONFIRM_PASSWORD
            )
        )
    }

    @Test
    fun testIsConfirmPasswordNotValid() {
        assert(signUpActivityViewModel.isConfirmPasswordValid(VALID_PASSWORD, INVALID_PASSWORD))
    }

    @Test
    fun testIsConfirmPasswordValidWhenNull() {
        Assert.assertFalse(signUpActivityViewModel.isConfirmPasswordValid(VALID_PASSWORD, null))
    }

    @Test
    fun testIsGenderValid() {
        assert(signUpActivityViewModel.isGenderValid(VALID_GENDER))
    }

    @Test
    fun testIsGenderNotValid() {
        Assert.assertFalse(signUpActivityViewModel.isGenderValid(INVALID_GENDER))
    }

    @Test
    fun testSignUp() {
        val user = User(
            email = VALID_MAIL,
            username = VALID_NAME,
            passwordConfirmation = VALID_CONFIRM_PASSWORD,
            password = VALID_PASSWORD,
            gender = VALID_GENDER
        )
        runBlocking { signUpActivityViewModel.signUp(user) }
        assert(signUpActivityViewModel.networkState == NetworkState.idle)
        assert(signUpActivityViewModel.state == SignUpState.signUpSuccess)
    }

    @Test
    fun testSignUpWhenFail() {
        val user = User(
            email = INVALID_MAIL,
            username = VALID_NAME,
            passwordConfirmation = VALID_CONFIRM_PASSWORD,
            password = VALID_PASSWORD,
            gender = VALID_GENDER
        )
        runBlocking { signUpActivityViewModel.signUp(user) }
        assert(signUpActivityViewModel.networkState == NetworkState.error)
        assert(signUpActivityViewModel.state == SignUpState.signUpFailure)
    }

    companion object {
        const val VALID_NAME = "Jane"
        const val INVALID_NAME = ""
        const val VALID_MAIL = "aj@gmail.com"
        const val INVALID_MAIL = "as.com"
        const val VALID_PASSWORD = "12345678"
        const val INVALID_PASSWORD = "123456"
        const val VALID_CONFIRM_PASSWORD = "12345678"
        const val VALID_GENDER = "other"
        const val INVALID_GENDER = ""
    }
}
