package com.rootstrap.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers
import com.google.gson.Gson
import com.rootstrap.android.R
import com.rootstrap.android.network.managers.SessionManager
import com.rootstrap.android.network.models.UserSerializer
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.ui.activity.main.authentication.SignInActivity
import com.rootstrap.android.ui.activity.main.authentication.SignUpActivity
import com.rootstrap.android.utils.BaseTests
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SignUpActivityTest : BaseTests() {

    private lateinit var activity: SignUpActivity
    private lateinit var scenario: ActivityScenario<SignUpActivity>

    @Before
    override fun before() {
        super.before()
        scenario = ActivityScenario.launch(SignUpActivity::class.java)
        scenario.onActivity { activity -> this.activity = activity }
    }

    @Test
    fun signUpSuccessfulTest() {
        scenario.recreate()
        setServerDispatch(signUpDispatcher())
        val testUser = testUser()
        scrollAndTypeText(R.id.first_name_edit_text, testUser.firstName)
        scrollAndTypeText(R.id.email_edit_text, testUser.email)
        scrollAndTypeText(R.id.password_edit_text, testUser.password)
        scrollAndTypeText(R.id.confirm_password_edit_text, testUser.passwordConfirmation)

        Espresso.onView(ViewMatchers.withId(R.id.gender_drop_down_text)).perform(
            ViewActions.scrollTo(),
            replaceText(testUser.gender))

        scrollAndPerformClick(R.id.sign_up_button)
        val user = SessionManager.user
        assertEquals(user, testUser)
        activity.runOnUiThread {
            val current = currentActivity()
            assertEquals(ProfileActivity::class.java.name, current::class.java.name)
        }
    }

    @Test
    fun checkCTASignIn() {
        scenario.recreate()
        scrollAndPerformClick(R.id.sign_in_text_view)
        activity.runOnUiThread {
            val current = currentActivity()
            assertEquals(SignInActivity::class.java.name, current::class.java.name)
        }
    }

    private fun signUpDispatcher(): Dispatcher {
        return object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return if (request.path!!.contains("users")) {
                    val userResponse = UserSerializer(testUser())
                    mockServer.successfulResponse().setBody(
                        Gson().toJson(userResponse)
                    )
                } else
                    mockServer.notFoundResponse()
            }
        }
    }

    @After
    override fun after() {
        super.after()
    }
}
