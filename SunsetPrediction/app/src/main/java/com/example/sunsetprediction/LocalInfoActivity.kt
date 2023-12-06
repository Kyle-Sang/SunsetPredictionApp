package com.example.sunsetprediction

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import androidx.appcompat.app.AppCompatActivity

class LocalInfoActivity : AppCompatActivity() {
    private lateinit var clock : AnalogClock
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_info)

        clock = findViewById(R.id.clock)

    }

    fun nextScreen(v : View) {
        var intent: Intent = Intent(this, RatingActivity::class.java)
        startActivity(intent) // go to Travel Activity
        Log.w("MapActivity", "to rating")
    }
}