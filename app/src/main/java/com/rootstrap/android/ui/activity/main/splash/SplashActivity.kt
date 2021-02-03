package com.rootstrap.android.ui.activity.main.splash

import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import com.rootstrap.android.databinding.ActivitySplashBinding
import com.rootstrap.android.ui.activity.main.authentication.AuthenticationActivity
import com.rootstrap.android.ui.base.BaseActivity

class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTransitionListener()
    }

    private fun setTransitionListener() {
        binding.splashScreenContainer.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = Unit

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                startActivityClearTask(AuthenticationActivity())
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.splashScreenContainer.startLayoutAnimation()
    }
}
