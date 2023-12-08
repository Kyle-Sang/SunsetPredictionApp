package com.example.sunsetprediction

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant
import java.util.Date
import java.util.Locale

class LocalInfoActivity : AppCompatActivity() {
    private lateinit var clock : AnalogClock
    private lateinit var pred_rating : RatingBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_info)

        clock = findViewById(R.id.clock)
        pred_rating = findViewById(R.id.rating)
        pred_rating.rating = getRating()

        Log.w("RatingActivity", "here!!!")

//        var coarseLocPerm : Int =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        var fineLocPerm : Int =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        if( coarseLocPerm == PackageManager.PERMISSION_GRANTED && fineLocPerm == PackageManager.PERMISSION_GRANTED) {
//            Log.w( "LocalInfoActivity", "GPS permissions already granted" )
//            // start using the camera
//        } else {
//            Log.w( "LocalInfoActivity", "Need to ask GPS permission" )
//            var contract : ActivityResultContracts.RequestPermission = ActivityResultContracts.RequestPermission()
//            var callback : Results = Results()
//            var launcher : ActivityResultLauncher<String> = registerForActivityResult(contract) { result ->
//                if (result) {
//                    Log.w("LocalInfoActivity", "Permission granted by user")
//                    // start using the camera
//                } else {
//                    Log.w("LocalInfoActivity", "Permission denied by user")
//                }
//            }
//            launcher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
//        }

        var provider : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.w("LocalInfoActivity", "permissions")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        var locTask: Task<Location> = provider.lastLocation
        locTask.addOnSuccessListener {loc ->
            Log.w("LocalInfoActivity", "location retrieved")
            if (loc != null) {
                Log.w("LocalInfoActivity", "not null")
                var task: WebApiThread = WebApiThread(this, loc.latitude, loc.longitude)
                task.start()
            } else {
                Log.w("LocalInfoActivity", "location null")
            }
        }

    }

    fun accessGPS() {

    }

    inner class Results : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean) {
            if( result ) {
                Log.w( "bruh", "Permission granted by user" )
                // start using the camera

            } else {
                Log.w( "bruh", "Permission denied by user" )
            }
        }
    }

    fun nextScreen(v : View) {
        var intent: Intent = Intent(this, RatingActivity::class.java)
        startActivity(intent) // go to Travel Activity
        Log.w("MapActivity", "to rating")
    }

    fun returnToMap(v : View) {
        finish()
    }

    private fun getRating(): Float {
        return (0..5).random().toFloat()
    }
    
    fun updateGui(s : String) {
        try {
            var info : JSONObject = JSONObject(s)
            var location : String = info.getString("name")

            var weather : String = info.getJSONArray( "weather" ).getJSONObject(0).getString("description")
            var mainInfo : JSONObject = info.getJSONObject("main")
            var temp : Double = (mainInfo.getDouble("temp") - 273.15) * 9 / 5 + 32
            var pressure : Int = mainInfo.getInt("pressure")
            var humidity : Int = mainInfo.getInt("humidity")
            var sunset : Date = Date.from(Instant.ofEpochSecond(info.getJSONObject("sys").getLong("sunset")))

            Log.w("LocalInfoActivity","location: $location")
            Log.w("LocalInfoActivity","weather: $weather")
            Log.w("LocalInfoActivity","temp: $temp")
            Log.w("LocalInfoActivity","pressure: $pressure")
            Log.w("LocalInfoActivity","humidity: $humidity")
            Log.w("LocalInfoActivity","sunset: $sunset")

            findViewById<TextView>(R.id.location).text = location
            findViewById<TextView>(R.id.description).text = weather.capitalize()
            findViewById<TextView>(R.id.temperature).text = temp.toString()
            findViewById<TextView>(R.id.sunset_time).text = sunset.toString()


        } catch ( je : JSONException) {
            Log.w( "RatingActivity", "Json exception is " + je.message )
        }
    }
}