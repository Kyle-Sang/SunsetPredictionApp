package com.example.sunsetprediction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.slaviboy.voronoi.Delaunay
import com.slaviboy.voronoi.Voronoi


class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // make sure you add at least 3 pairs of x,y coordinates
        val coordinates: DoubleArray = doubleArrayOf(
            19.0, 93.0,   // first point
            1.0, 64.0,    // second point
            23.0, 93.0,   // third point
            192.0, 43.0,  // fourth point
            14.0, 2.0     // fifth point
        )

        var map : VoronoiView = VoronoiView(this)
        var mapView : RelativeLayout = findViewById(R.id.map)
        mapView.addView(map)
    }

    fun nextScreen (v : View) {
        var intent = Intent(this, LocalInfoActivity::class.java)
        startActivity(intent) // go to LocalInfoActivity
    }

    companion object {
        var sunset_pred : SunsetPredictor = SunsetPredictor()
    }
}