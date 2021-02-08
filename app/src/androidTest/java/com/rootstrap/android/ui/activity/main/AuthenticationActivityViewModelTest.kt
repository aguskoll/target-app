package com.rootstrap.android.ui.activity.main

import com.rootstrap.android.network.managers.UserManager
import com.rootstrap.android.ui.activity.main.authentication.SignInActivityViewModel
import com.rootstrap.android.util.ViewModelListener
import com.rootstrap.android.utils.BaseTests
import junit.framework.Assert.assertFalse
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthenticationActivityViewModelTest : BaseTests() {

    private lateinit var viewModel: SignInActivityViewModel

    @Before
    fun setUp() {
        super.before()
        viewModel = SignInActivityViewModel(
            object : ViewModelListener {
                override fun updateState() {}

                override fun updateNetworkState() {}
            },
            UserManager
        )
    }

    @Test
    fun testCanSignIn() {
        val user = testUser()
        assert(viewModel.canSignIn(user.email, user.password))
    }

    @Test
    fun testCanSignInWhenNot() {
        val user = testUser()
        assertFalse(viewModel.canSignIn("", user.password))
    }

    @After
    override fun after() {
        super.after()
    }
}
