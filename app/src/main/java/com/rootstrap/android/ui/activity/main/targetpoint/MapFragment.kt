package com.rootstrap.android.ui.activity.main.targetpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rootstrap.android.R
import com.rootstrap.android.databinding.FragmentMapBinding
import com.rootstrap.android.util.permissions.PermissionFragment
import com.rootstrap.android.util.permissions.PermissionResponse
import com.rootstrap.android.util.permissions.checkNotGrantedPermissions
import com.rootstrap.android.util.permissions.locationPermissions

class MapFragment : PermissionFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: FragmentMapBinding
    private lateinit var createTargetViewModel: CreateTargetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        val factory = CreateTargetViewModelViewModelFactory()
        createTargetViewModel = ViewModelProvider(this, factory).get(CreateTargetViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (requireContext().checkNotGrantedPermissions(locationPermissions).isEmpty()) {
            getDeviceLocation()
        } else {
            askForLocationPermission {
                getDeviceLocation()
            }
        }
    }

    private fun getDeviceLocation() {
        try {
            mMap.isMyLocationEnabled = true
            createTargetViewModel.getDeviceLocation(requireContext()) { location ->
                with(location) {
                    addMarker(latitude, longitude)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun addMarker(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_location_marker))
        )
        drawCircle(location)
        setMapCamera(location)
    }

    private fun drawCircle(point: LatLng) {
        val circleOptions = CircleOptions().apply {
            center(point)
            radius(CIRCLE_RADIUS)
            strokeColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            fillColor(ContextCompat.getColor(requireContext(), R.color.whiteTransparent))
            strokeWidth(CIRCLE_STROKE_WIDTH)
        }

        mMap.addCircle(circleOptions)
    }

    private fun setMapCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().apply {
            target(latLng)
            zoom(GOOGLE_MAPS_ZOOM)
            bearing(GOOGLE_MAPS_BEARING)
            tilt(GOOGLE_MAPS_TILT)
        }.build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun askForLocationPermission(permissionGranted: () -> Unit) {
        requestPermission(
            locationPermissions,
            object : PermissionResponse {
                override fun granted() {
                    permissionGranted()
                }

                override fun denied() = Unit

                override fun foreverDenied() = Unit
            })
    }

    companion object {
        const val GOOGLE_MAPS_ZOOM = 15f
        const val GOOGLE_MAPS_BEARING = 0f
        const val GOOGLE_MAPS_TILT = 5f
        const val CIRCLE_RADIUS = 90.0
        const val CIRCLE_STROKE_WIDTH = 2f

        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
