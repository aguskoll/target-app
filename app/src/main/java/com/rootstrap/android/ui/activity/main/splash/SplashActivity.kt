package com.rootstrap.android.ui.activity.main.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.rootstrap.android.R
import com.rootstrap.android.ui.activity.main.authentication.AuthenticationActivity
import com.rootstrap.android.ui.base.BaseActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityClearTask(AuthenticationActivity())
            finish()
        }, 1500)
    }
}
