package com.example.sunsetprediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity

class LocalInfoActivity : AppCompatActivity() {
    private lateinit var clock : AnalogClock
    private lateinit var pred_rating : RatingBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_info)

        clock = findViewById(R.id.clock)
        pred_rating = findViewById(R.id.rating)
        pred_rating.rating = getRating()
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
}