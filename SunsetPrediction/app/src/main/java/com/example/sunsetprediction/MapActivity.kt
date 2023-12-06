package com.example.sunsetprediction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var task : WebApiThread = WebApiThread( 38.982, -76.943)
        task.start()
    }
}