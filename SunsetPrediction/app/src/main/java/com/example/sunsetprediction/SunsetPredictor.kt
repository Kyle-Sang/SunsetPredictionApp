package com.example.sunsetprediction

import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView

class SunsetPredictor {
    private lateinit var editor : SharedPreferences.Editor

    fun inputGPSData() {

    }

    fun queryAPIData() {
        // input lat long, grab all data
    }

    fun getPredictedRating(data : WeatherData): Float {
        // ML model to be imported
        return (1..5).random().toFloat()
    }

    fun initiatePreferences(context : Context, ratings_count : TextView) {
        val pref : SharedPreferences = context.getSharedPreferences(context.packageName + "_preferences",
            Context.MODE_PRIVATE)
        editor = pref.edit()

        RatingActivity.num_user_ratings = pref.getInt(PREFERENCE_NUM_RATINGS, 0)
        ratings_count.text = "Total ratings contributed ${RatingActivity.num_user_ratings}"
        // store via persistent data
        // send data to server (S3 bucket) once page is closed
    }

    // gets previous number of user ratings
    // updates value to include new rating
    fun updatePreferences() {
        RatingActivity.num_user_ratings += 1
        editor.putInt(PREFERENCE_NUM_RATINGS, RatingActivity.num_user_ratings)
        editor.commit()
    }

    companion object {
        const val PREFERENCE_NUM_RATINGS : String = "rating"
    }
}