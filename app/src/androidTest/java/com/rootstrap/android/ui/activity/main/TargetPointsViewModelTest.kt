package com.rootstrap.android.ui.activity.main

import com.rootstrap.android.network.managers.LocationManager
import com.rootstrap.android.network.managers.TargetPointManager
import com.rootstrap.android.ui.activity.main.targetpoint.TargetPointsViewModel
import com.rootstrap.android.utils.BaseTests
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test

class TargetPointsViewModelTest : BaseTests() {

    private val targetPointsViewModel: TargetPointsViewModel = TargetPointsViewModel(TargetPointManager, LocationManager)

    @Before
    fun setUp() {
        super.before()
    }

    @Test
    fun testIsLocationStateSuccess() {
        Assert.assertFalse(targetPointsViewModel.isLocationStateSuccess())
    }

    @Test
    fun testIsAreaValid() {
        assert(targetPointsViewModel.isAreaValid(AREA_VALID))
    }

    @Test
    fun testAreaInValid() {
        Assert.assertFalse(targetPointsViewModel.isAreaValid(AREA_INVALID))
    }

    @Test
    fun testIsTitleValid() {
        assert(targetPointsViewModel.isTitleValid(TITLE_VALID))
    }

    @Test
    fun testIsTitleInValid() {
        Assert.assertFalse(targetPointsViewModel.isTitleValid(TITLE_INVALID))
    }

    @Test
    fun testIsTopicValid() {
        assert(targetPointsViewModel.isTopicValid(TOPIC_VALID))
    }

    @Test
    fun testIsTopicInValid() {
        Assert.assertFalse(targetPointsViewModel.isTopicValid(TOPIC_INVALID))
    }

    @After
    override fun after() {
        super.after()
    }

    companion object {
        const val AREA_VALID = 200.0
        const val AREA_INVALID = 0.0
        const val TITLE_VALID = "target title"
        const val TITLE_INVALID = ""
        const val TOPIC_VALID = 12
        const val TOPIC_INVALID = 0
    }
}
