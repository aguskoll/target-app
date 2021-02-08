package com.rootstrap.android.ui.activity.main.targetpoint

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.ui.base.BaseActivity
import com.rootstrap.android.util.NetworkState

class TargetPointsActivity : BaseActivity() {

    private lateinit var binding: ActivityTargetPointsBinding

    private lateinit var createTargetView: CreateTargetView

    private lateinit var targetPointsViewModel: TargetPointsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTargetPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = CreateTargetViewModelViewModelFactory()
        targetPointsViewModel = ViewModelProvider(this, factory).get(TargetPointsViewModel::class.java)

        observeNetworkState()

        initView()
    }

    private fun observeNetworkState() {
        targetPointsViewModel.networkStateObservable.observe(this, Observer { state ->
            state?.run {
                when (state) {
                    NetworkState.loading -> showProgress()
                    NetworkState.error, NetworkState.idle -> hideProgress()
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
        createTargetView = CreateTargetView(binding, targetPointsViewModel, this)
        binding.goToTargetContainer.setOnClickListener {
            createTargetView.expandCollapseCreateTargetSheet()
        }
    }

    companion object {
        const val PICK_HEIGHT_HIDDEN = 0
    }
}
