package com.rootstrap.android.tests

import androidx.test.core.app.ActivityScenario
import com.rootstrap.android.R
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.ui.activity.main.targetpoint.TargetPointsActivity
import com.rootstrap.android.utils.BaseTests
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TargetPointsActivityTest : BaseTests() {

    private lateinit var activity: TargetPointsActivity
    private lateinit var scenario: ActivityScenario<TargetPointsActivity>

    @Before
    override fun before() {
        super.before()
        setupSession()
        scenario = ActivityScenario.launch(TargetPointsActivity::class.java)
        scenario.onActivity { activity -> this.activity = activity }
    }

    @Test
    fun testGoToProfile() {
        scenario.recreate()
        performClick(R.id.go_to_profile_btn)
        activity.runOnUiThread {
            val current = currentActivity()
            Assert.assertEquals(ProfileActivity::class.java.name, current::class.java.name)
        }
    }
}
