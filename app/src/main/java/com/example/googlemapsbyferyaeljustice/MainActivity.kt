package com.example.googlemapsbyferyaeljustice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMap()
    }

    // Create the map in an async for after assign it to our variable map
    private fun createMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // When google map is ready, we instantiate it and put its properties
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        createMarker()
        createPolyline()
        enableLocation()
    }

    // Creates a polyline
    private fun createPolyline() {
        val polylineOptions = PolylineOptions()
            .add(LatLng(39.57261615976338, 2.621440887451172))
            .add(LatLng(39.57936413324042, 2.6289939880371094))
            .add(LatLng(39.576585635482296, 2.6383495330810547))
            .add(LatLng(39.57023436522896, 2.6352596282958984))
            .add(LatLng(39.56864645674722, 2.625560760498047))
            .add(LatLng(39.569043437277045, 2.6259899139404297))
            .width(30f)
            .color(ContextCompat.getColor(this, R.color.kotlin))

        val polyline = map.addPolyline(polylineOptions)

        polyline.startCap = RoundCap()
        polyline.endCap =
            CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
        val pattern = listOf(
            Dot(), Gap(10f), Dash(50f), Gap(10f)
        )
        polyline.pattern = pattern

        polyline.isClickable = true
        map.setOnPolylineClickListener { poly ->
            changeColor(poly)
        }
    }

    // Modifies randomly a polyline color
    private fun changeColor(polyline: Polyline) {
        when ((0..3).random()) {
            0 -> polyline.color = ContextCompat.getColor(this, R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.yellow)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.green)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.blue)
        }
    }

    // Creates a defined marker
    private fun createMarker() {
        // Centro palma
        val centroPalma = LatLng(39.575914, 2.653830)
        map.addMarker(MarkerOptions().position(centroPalma).title("Centro de Palma"))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(centroPalma, 18f),
            4000,
            null
        )
    }

    // Checks the location permission
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Enable location button depending if it has the permission or not
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            // Si
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            // No
            requestLocationPermission()
        }
    }

    // Check if the user previously denied the location permission, if not request it
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            requestLocationPermissionFinale()
        }
    }

    // Request Location Permission
    private fun requestLocationPermissionFinale() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION
        )
    }

    // When the system on this app has made changes or not on the permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    // When fragments are resumed
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            map.isMyLocationEnabled = false
        } else {
            Toast.makeText(
                this,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Button locate
    override fun onMyLocationButtonClick(): Boolean {
        // return false va a tu localizacion, true no
        //Toast.makeText(this, "Botón pulsado", Toast.LENGTH_SHORT).show()
        return if (isLocationServiceEnabled()) {
            false
        } else {
            Toast.makeText(this, "Please activate the GPS.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    // User click on current location dot (info)
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }


    // Check if GPS is enabled in the system (NOT PERMISSION)
    private fun isLocationServiceEnabled(): Boolean {
        val lm: LocationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}