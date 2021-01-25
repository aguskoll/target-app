package com.rootstrap.android.ui.activity.main.targetpoint

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ActivityTargetPointsBinding
import com.rootstrap.android.ui.activity.main.authentication.ProfileActivity
import com.rootstrap.android.util.NetworkState
import com.rootstrap.android.util.extensions.hideKeyboard
import com.rootstrap.android.ui.base.BaseActivity

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

        observeCreateTargetState()

        observeNetworkState()

        initView()
    }

    private fun observeCreateTargetState() {
        targetPointsViewModel.createTargetState.observe(this, Observer { targetState ->
            targetState?.run {
                when (this) {
                    CreateTargetState.fail -> showError(targetPointsViewModel.error ?: getString(R.string.default_error))
                    CreateTargetState.success -> {
                        successCreatingTarget()
                    }
                    CreateTargetState.none -> Unit
                }
            }
        })
    }

    // TODO: show target in map and remove toast
    private fun successCreatingTarget() {
        createTargetView.expandCollapseSheet()
        hideKeyboard()
        Toast.makeText(this, "success creating target", Toast.LENGTH_SHORT).show()
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
        createTargetView = CreateTargetView(binding, targetPointsViewModel)
        binding.goToTargetContainer.setOnClickListener {
            createTargetView.expandCollapseSheet()
        }
    }

    companion object {
        const val PICK_HEIGHT_HIDDEN = 0
    }
}
