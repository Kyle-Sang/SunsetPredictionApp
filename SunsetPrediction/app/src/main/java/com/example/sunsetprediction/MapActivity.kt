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