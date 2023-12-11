package com.example.sunsetprediction

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView

class SunsetPredictor {
    private lateinit var editor : SharedPreferences.Editor
    private var humidity = -1f
    private var pressure = -1f
    private var cloud = -1f
    private var prediction = -1f

    fun getPredictedRating(data : WeatherData): Float {
        // ML model to be imported
        // center most optimal cloud cover at 40% cloud cover
        cloud = (100 - (kotlin.math.abs(data.clouds - 40))) / 100
        // reward lower hpa
        pressure = (1050 - data.pressure) / 100
        // reward lower humidity
        humidity = (130 - data.humidity) / 100
        prediction = (humidity + pressure + cloud) * 5 / 3
        return kotlin.math.min(prediction, 5f)
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

    fun getRating(userRate : Float) : Rating {
        return Rating(cloud, humidity, pressure, prediction, userRate)
    }

    companion object {
        const val PREFERENCE_NUM_RATINGS : String = "rating"
    }
}