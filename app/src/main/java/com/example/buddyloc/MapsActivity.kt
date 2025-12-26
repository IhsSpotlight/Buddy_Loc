package com.example.buddyloc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlin.math.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var firestoreViewModel: FirestoreViewModel

    private var myLat = 0.0
    private var myLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        myLat = intent.getDoubleExtra("lat", 0.0)
        myLng = intent.getDoubleExtra("lng", 0.0)

        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.clear()

        if (myLat == 0.0 || myLng == 0.0) return

        val myLocation = LatLng(myLat, myLng)

        // 🔵 My marker
        googleMap.addMarker(
            MarkerOptions()
                .position(myLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        // 🎯 Move camera immediately to MY location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))

        // 🔴 Listen to other users
        firestoreViewModel.listenToUsers { userList ->

            googleMap.clear()

            val boundsBuilder = LatLngBounds.Builder()

            // Add my marker again
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            boundsBuilder.include(myLocation)

            for (user in userList) {

                val lat = user.latitude
                val lng = user.longitude

                if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {

                    val userLocation = LatLng(lat, lng)

                    val distance = calculateDistance(myLat, myLng, lat, lng)

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(userLocation)
                            .title(user.displayName ?: "User")
                            .snippet("Distance: ${"%.2f".format(distance)} km")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )

                    boundsBuilder.include(userLocation)
                }
            }

            // 📍 Auto zoom to show all markers
            /**val bounds = boundsBuilder.build()
            googleMap.setOnMapLoadedCallback {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
            }**/
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {

        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
