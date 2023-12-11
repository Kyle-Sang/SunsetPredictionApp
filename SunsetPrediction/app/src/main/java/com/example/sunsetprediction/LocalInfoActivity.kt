package com.example.sunsetprediction

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class LocalInfoActivity : AppCompatActivity(), LocationListener {
    private lateinit var clock : AnalogClock
    private lateinit var pred_rating : RatingBar
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var ad : InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_info)

        var adUnitId : String = // "ca-app-pub-3940256099942544/6300978111"
            "ca-app-pub-3940256099942544/1033173712"
        var adRequest : AdRequest = (AdRequest.Builder( )).build( )
        var adLoad : AdLoad = AdLoad( )
        InterstitialAd.load( this, adUnitId, adRequest, adLoad )
        var data : WeatherData = WeatherData(0f, 0f)

        clock = findViewById(R.id.clock)
        pred_rating = findViewById(R.id.rating)
        pred_rating.rating = MapActivity.sunset_pred.getPredictedRating(data)

        Log.w("RatingActivity", "here!!!")

        getLocation()
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
        var intent = Intent(this, RatingActivity::class.java)
        startActivity(intent) // go to RatingActivity
    }

    fun returnToMap(v : View) {
        finish()
    }
    
    fun updateGui(s : String) {
        try {
            // Get values out of JSON response
            var info : JSONObject = JSONObject(s)
            var location : String = info.getString("name")
            var weather : String = info.getJSONArray( "weather" ).getJSONObject(0).getString("description")
            var mainInfo : JSONObject = info.getJSONObject("main")
            var temp : Double = (mainInfo.getDouble("temp") - 273.15) * 9 / 5 + 32
            var pressure : Int = mainInfo.getInt("pressure")
            var humidity : Int = mainInfo.getInt("humidity")
            var sunset : Instant = Instant.ofEpochSecond(info.getJSONObject("sys").getLong("sunset"))

            // Log retrieved values
            Log.w("LocalInfoActivity","location: $location")
            Log.w("LocalInfoActivity","weather: $weather")
            Log.w("LocalInfoActivity","temp: $temp")
            Log.w("LocalInfoActivity","pressure: $pressure")
            Log.w("LocalInfoActivity","humidity: $humidity")
            Log.w("LocalInfoActivity","sunset: $sunset")

            // Format sunset time
            val formatter = DateTimeFormatter.ofPattern("h:ma").withZone(ZoneId.systemDefault())
            val sunsetFormatted  = formatter.format(sunset)

            // Set text view values
            findViewById<TextView>(R.id.location).text = location
            findViewById<TextView>(R.id.description).text = weather.capitalize()
            findViewById<TextView>(R.id.temperature).text = "${DecimalFormat("#").format(temp)}\u2109"
            findViewById<TextView>(R.id.sunset_time).text = "Sun sets at ${sunsetFormatted}"
        } catch ( je : JSONException) {
            Log.w( "RatingActivity", "Json exception is " + je.message )
        }
    }

    private fun getLocation() {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

        }
    }
    override fun onLocationChanged(location: Location) {
        var task: WebApiThread = WebApiThread(this, location.latitude, location.longitude)
        task.start()
    }

    inner class AdLoad : InterstitialAdLoadCallback( ) {
        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            Log.w( "MainActivity", "ad failed to load" )
        }

        override fun onAdLoaded(p0: InterstitialAd) {
            super.onAdLoaded(p0)
            Log.w( "MainActivity", "ad loaded" )
            // show the ad
            ad = p0
            ad!!.show( this@LocalInfoActivity )

            // handle user interaction with the ad
            var manageAd : ManageAdShowing = ManageAdShowing()
            ad!!.fullScreenContentCallback = manageAd
        }
    }
    inner class ManageAdShowing : FullScreenContentCallback( ) {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.w( "MainActivity", "user closed the ad" )
            // some code here to continue the app
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.w( "MainActivity", "User clicked on the ad" )
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.w( "MainActivity", "user has seen the ad" )
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.w( "MainActivity", "ad has been shown" )
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            Log.w( "MainActivity", "ad failed to show" )
        }
    }
}