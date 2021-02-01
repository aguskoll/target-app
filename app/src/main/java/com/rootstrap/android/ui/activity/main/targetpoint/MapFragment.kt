package com.rootstrap.android.ui.activity.main.targetpoint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rootstrap.android.R
import com.rootstrap.android.databinding.FragmentMapBinding
import com.rootstrap.android.models.TargetModel
import com.rootstrap.android.util.extensions.getIconForTarget
import com.rootstrap.android.util.permissions.PermissionFragment
import com.rootstrap.android.util.permissions.PermissionResponse
import com.rootstrap.android.util.permissions.checkNotGrantedPermissions
import com.rootstrap.android.util.permissions.locationPermissions

class MapFragment : PermissionFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: FragmentMapBinding
    private lateinit var targetPointsViewModel: TargetPointsViewModel

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
        targetPointsViewModel = ViewModelProvider(requireActivity(), factory).get(TargetPointsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTargets()
        observeNewTargets()
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
            targetPointsViewModel.getDeviceLocation(requireContext()) { location ->
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

    private fun addCircleMarkerForTarget(target: TargetModel) {
        val position = LatLng(target.lat, target.lng)
        target.topic?.run {
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .icon(bitmapDescriptorwithOvalBackground(requireContext(), getIconForTarget()))
            )
        }
    }

    private fun bitmapDescriptorwithOvalBackground(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, R.drawable.bc_oval_marker)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val backgroundWidth = background?.intrinsicWidth ?: 0
        val backgroundHeight = background?.intrinsicHeight ?: 0
        val drawableWidth = vectorDrawable?.intrinsicWidth ?: 0
        val drawableHeight = vectorDrawable?.intrinsicHeight ?: 0

        background?.setBounds(0, 0, backgroundWidth, backgroundHeight)

        vectorDrawable?.setBounds(
            0,
            0,
            drawableWidth,
            drawableHeight
        )

        val bitmap = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        background?.draw(canvas)

        canvas.translate(
            (backgroundWidth / 2 - drawableWidth / 2).toFloat(),
            (backgroundHeight / 2 - drawableHeight / 2).toFloat()
        )
        vectorDrawable?.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    private fun observeTargets() {
        targetPointsViewModel.targets.observe(viewLifecycleOwner, Observer {
            it.forEach { target ->
                addCircleMarkerForTarget(target)
            }
        })
    }

    private fun observeNewTargets() {
        targetPointsViewModel.newTarget.observe(viewLifecycleOwner, Observer {
            addCircleMarkerForTarget(it)
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
