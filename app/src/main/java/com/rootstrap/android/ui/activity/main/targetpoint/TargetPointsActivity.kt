package com.rootstrap.android.ui.activity.main.targetpoint

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.util.permissions.PermissionActivity
import com.rootstrap.android.util.permissions.PermissionResponse

class TargetPointsActivity : PermissionActivity(), MapFragment.MapFragmentInteraction {

    private lateinit var binding: ActivityTargetPointsBinding

    private lateinit var createTargetView: CreateTargetView

    private lateinit var createTargetViewModel: CreateTargetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTargetPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = CreateTargetViewModelViewModelFactory()
        createTargetViewModel = ViewModelProvider(this, factory).get(CreateTargetViewModel::class.java)

        observeCreateTargetState()

        initView()
    }

    private fun observeCreateTargetState() {
        createTargetViewModel.createTargetState.observe(this, Observer { targetState ->
            targetState?.run {
                when (this) {
                    CreateTargetState.fail -> showError(createTargetViewModel.error ?: getString(R.string.default_error))
                    CreateTargetState.none, CreateTargetState.success -> {
                    }
                }
            }
        })
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
        initCreateTargetView()
    }

    private fun initMapFragment() {
        val fragment = MapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_map, fragment).commit()
    }

    private fun initCreateTargetView() {
        createTargetView = CreateTargetView(binding, createTargetViewModel)
        binding.goToTargetContainer.setOnClickListener {
            createTargetView.expandCollapseSheet()
        }
    }

    override fun askForLocationPermission(permissionGranted: () -> Unit) {
        requestPermission(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
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

    companion object {
        const val PICK_HEIGHT_HIDDEN = 0
    }
}
