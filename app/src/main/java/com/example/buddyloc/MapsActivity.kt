package com.example.buddyloc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var firestoreViewModel: FirestoreViewModel

    private var myLat = 0.0
    private var myLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // ✅ Receive current user location
        myLat = intent.getDoubleExtra("lat", 0.0)
        myLng = intent.getDoubleExtra("lng", 0.0)

        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // ✅ Move camera to MY location
        if (myLat != 0.0 && myLng != 0.0) {
            val myLocation = LatLng(myLat, myLng)
            googleMap.addMarker(
                MarkerOptions()
                    .position(myLocation)
                    .title("You are here")
            )
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(myLocation, 16f)
            )
        }

        // ✅ Show other users
        firestoreViewModel.getAllUsers { userList ->
            for (user in userList) {
                val locationString = user.latitude.toString() + ", " + user.longitude.toString()

                if (!locationString.isNullOrEmpty() && locationString.contains("Lat")) {
                    val latLng = parseLocationSafely(locationString)
                    if (latLng != null) {
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(user.DisplayName)
                        )
                    }
                }
            }
        }
    }

    // ✅ SAFE parsing (no crash)
    private fun parseLocationSafely(location: String): LatLng? {
        return try {
            val parts = location.split(", ")
            val lat = parts[0].substringAfter("Lat: ").toDouble()
            val lng = parts[1].substringAfter("Long: ").toDouble()
            LatLng(lat, lng)
        } catch (e: Exception) {
            null
        }
    }
}
