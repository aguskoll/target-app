package com.rootstrap.android.ui.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rootstrap.android.R

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initMapFragment()
    }

    private fun initMapFragment() {
        val fragment = MapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_map, fragment).commit()
    }
}
