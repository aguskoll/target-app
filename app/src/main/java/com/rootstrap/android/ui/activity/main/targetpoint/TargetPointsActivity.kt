package com.rootstrap.android.ui.activity.main.targetpoint

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.util.permissions.PermissionActivity
import com.rootstrap.android.util.permissions.PermissionResponse

class TargetPointsActivity : PermissionActivity(), MapFragment.MapFragmentInteraction {

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

    override fun askForLocationPermission(permissionGranted: () -> Unit) {
        requestPermission(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            object : PermissionResponse {
                override fun granted() {
                    permissionGranted()
                }

                override fun denied() = Unit

                override fun foreverDenied() = Unit
            })
    }
}
