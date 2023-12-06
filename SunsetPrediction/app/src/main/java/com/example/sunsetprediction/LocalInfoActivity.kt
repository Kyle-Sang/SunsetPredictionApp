package com.example.sunsetprediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant
import java.util.Date

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
        var task : WebApiThread = WebApiThread( this,38.982, -76.943)
        task.start()
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

        } catch ( je : JSONException) {
            Log.w( "RatingActivity", "Json exception is " + je.message )
        }
    }
}