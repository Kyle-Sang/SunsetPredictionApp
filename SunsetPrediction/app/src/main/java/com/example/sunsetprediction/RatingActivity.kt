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
    private var num_user_ratings : Int = 0
    private var rating : Float = 0f
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        rt = findViewById(R.id.ratingBar)
        ratings_count = findViewById(R.id.ratings_count)

        var pref : SharedPreferences= this.getSharedPreferences(this.packageName + "_preferences",
            Context.MODE_PRIVATE)
        editor = pref.edit()

        num_user_ratings = pref.getInt(PREFERENCE_NUM_RATINGS, 0)
        ratings_count.text = "Total ratings contributed ${num_user_ratings}"
        // store via persistent data
        // send data to server (S3 bucket) once page is closed
    }

    fun saveRating(v : View) {
        Log.w("RatingActivity", "save rating")
        rating = rt.rating
        setPreferences()
        Log.w("RatingActivity", rating.toString())
        finish()
    }

    // stores number of ratings
    private fun setPreferences() {
        Log.w("RatingActivity", "set preferences")
        // gets previous number of user ratings
        // updates value to include new rating
        num_user_ratings += 1
        editor.putInt(PREFERENCE_NUM_RATINGS, num_user_ratings)
        editor.commit()
    }

    companion object {
        private const val PREFERENCE_NUM_RATINGS : String = "rating"
    }
}