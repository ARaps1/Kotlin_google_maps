package com.example.works

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.works.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    // This will store the current location
    private lateinit var currentLocation: Location
    // This is the provider will use to fetch location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This initializes the provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // call to start fetching user location
        getUserCurrentLocation()
    }

    private fun getUserCurrentLocation() {
        // Checks if both permissions are disabled then request permissions
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }

        // If it makes it to this line, it means we have permission.

        //This uses the location provider and if it was successful runs the ccode inside {}
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            // location could be null if the user turns location services even though the app has permission
            if (location != null) {
                //stores current location
                currentLocation = location
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" + currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()

                /*
                The mapFragment is moved here that way it would only if the user has location enabled.
                If you want it to load even without location services you can move this back to OnCreate
                 */
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // This is switch statement, so if request code is equal to permissionCode then run the things inside
        when(requestCode) {
            // if grantResults has permissionGranted, it calls getuserCurrentLocation() which should
            // get past the if statement inside getUserCurrentLocation().
            permissionCode -> if(grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                getUserCurrentLocation()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        // Now once the map is ready, we can use our latLng to do things with the map
        val latLng = LatLng(currentLocation.latitude,currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Current Location")
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
        mMap.addMarker(markerOptions)
    }
}
