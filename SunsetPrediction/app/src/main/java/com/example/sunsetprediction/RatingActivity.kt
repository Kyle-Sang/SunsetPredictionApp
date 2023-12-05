package com.example.sunsetprediction

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity

class RatingActivity : AppCompatActivity() {
    private lateinit var rt : RatingBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        rt = findViewById(R.id.ratingBar);
        var rating = rt.numStars
        Log.w("RatingActivity", rating.toString())

        // store via persistent data
        // send data to server (S3 bucket) once page is closed
    }
}