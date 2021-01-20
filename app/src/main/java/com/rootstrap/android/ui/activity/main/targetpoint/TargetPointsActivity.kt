package com.rootstrap.android.ui.activity.main.targetpoint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity

class TargetPointsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTargetPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTargetPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        initMapFragment()
        binding.toolbar.goToProfileBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }
    }

    private fun initMapFragment() {
        val fragment = MapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_map, fragment).commit()
    }
}
