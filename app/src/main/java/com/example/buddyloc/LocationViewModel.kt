package com.example.buddyloc

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class LocationViewModel : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun initializeFusedLocationClient(client: FusedLocationProviderClient) {
        fusedLocationClient = client
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
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
