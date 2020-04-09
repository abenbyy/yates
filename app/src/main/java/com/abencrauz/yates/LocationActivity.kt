package com.abencrauz.yates

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var ZOOM_LEVEL = 17f
    private var location:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intent = intent
        location = intent.getStringExtra("location")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(location,1)
        var address = addresses.get(0)
        // Add a marker in Sydney and move the camera
        val marker = LatLng(address.latitude, address.longitude)
        mMap.addMarker(MarkerOptions().position(marker).title(location))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,ZOOM_LEVEL))

    }
}
