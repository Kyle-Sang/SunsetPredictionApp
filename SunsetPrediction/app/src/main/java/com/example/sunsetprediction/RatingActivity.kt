package com.example.sunsetprediction

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class RatingActivity : AppCompatActivity() {
    private lateinit var rt : RatingBar
    private lateinit var ratings_count : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        rt = findViewById(R.id.ratingBar)
        ratings_count = findViewById(R.id.ratings_count)
    }

    fun getRating(rt : RatingBar) : Float {
        return rt.rating
    }
    fun saveRating(v : View) {
        rating = rt.rating
        MapActivity.sunset_pred.updatePreferences()
        finish()
    }

    companion object {
        var rating : Float = 0f
        var num_user_ratings = 0
    }
}