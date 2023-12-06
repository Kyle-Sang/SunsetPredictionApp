package com.example.sunsetprediction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MapActivity : AppCompatActivity() {
    private lateinit var toLocal : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    fun nextScreen (v : View) {
        var intent: Intent = Intent(this, LocalInfoActivity::class.java)
        startActivity(intent) // go to Travel Activity
        Log.w("MapActivity", "to local info")
    }
}