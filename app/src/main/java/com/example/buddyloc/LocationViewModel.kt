package com.example.buddyloc

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class LocationViewModel : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun initializeFusedLocationClient(client: FusedLocationProviderClient) {
        fusedLocationClient = client
    }

    fun getLastLocation(callback: (Location?) -> Unit) {
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location ->
                callback(location)
            }
            ?.addOnFailureListener {
                callback(null)
            }
    }
}
