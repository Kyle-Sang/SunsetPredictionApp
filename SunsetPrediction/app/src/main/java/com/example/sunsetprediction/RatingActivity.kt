package com.example.sunsetprediction

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text
import kotlin.random.Random

class RatingActivity : AppCompatActivity() {
    private lateinit var rt : RatingBar
    private lateinit var ratings_count : TextView
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        rt = findViewById(R.id.ratingBar)
        ratings_count = findViewById(R.id.ratings_count)
        MapActivity.sunset_pred.initiatePreferences(this, ratings_count)
        database = Firebase.database.reference
    }

    fun getRating(rt : RatingBar) : Float {
        return rt.rating
    }
    fun saveRating(v : View) {
        rating = rt.rating
        MapActivity.sunset_pred.updatePreferences()
        val userid = Random.nextInt(1000, 9999)
        val currentRating = MapActivity.sunset_pred.getRating(rt.rating)
        database.child("Ratings").push().setValue(currentRating)
        finish()
    }

    fun goBack(v : View) {
        finish()
    }

    companion object {
        var rating : Float = 0f
        var num_user_ratings = 0
    }
}