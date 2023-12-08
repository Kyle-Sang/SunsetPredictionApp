package com.example.sunsetprediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        // get gps data
        // ask permission for this

        // update these with GPS returned values
        var data : WeatherData = WeatherData(0f, 0f)

        clock = findViewById(R.id.clock)
        pred_rating = findViewById(R.id.rating)
        pred_rating.rating = MapActivity.sunset_pred.getPredictedRating(data)

        // replace these with GPS returned values
        var task = WebApiThread( this,38.982, -76.943)
        task.start()
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
            var info = JSONObject(s)
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