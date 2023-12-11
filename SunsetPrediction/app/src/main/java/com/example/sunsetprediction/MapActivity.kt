package com.example.sunsetprediction

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.slaviboy.voronoi.Delaunay
import com.slaviboy.voronoi.Voronoi

import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MapActivity : AppCompatActivity() {
    private var locationEnabled : Boolean = false
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // make sure you add at least 3 pairs of x,y coordinates
        val coordinates: DoubleArray = doubleArrayOf(
            19.0, 93.0,   // first point
            1.0, 64.0,    // second point
            23.0, 93.0,   // third point
            192.0, 43.0,  // fourth point
            14.0, 2.0     // fifth point
        )

        var map : VoronoiView = VoronoiView(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationEnabled = true
        }
        var mapView : RelativeLayout = findViewById(R.id.map)
        mapView.addView(map)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.w("Map Activity", "Permission Granted")
                locationEnabled = true
            }
            else {
                Log.w("Map Activity", "Permission Denied")
            }
        }
    }

    fun nextScreen (v : View) {
        if (locationEnabled) {
            var intent = Intent(this, LocalInfoActivity::class.java)
            startActivity(intent) // go to LocalInfoActivity
        } else {
            Toast.makeText(this, "Cannot get current location. Check that location services are enabled?", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        var sunset_pred : SunsetPredictor = SunsetPredictor()
    }
}