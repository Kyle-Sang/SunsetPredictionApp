package com.example.sunsetprediction

import android.app.Activity
import android.util.Log
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class WebApiThread : Thread {
    lateinit var activity : Activity
    var result : String = "NOT SET YET"
    private var lat : Double = 0.0
    private var lon : Double = 0.0

    constructor( lat : Double, lon : Double) {
        this.lat = lat
        this.lon = lon
    }

    override fun run( ) {
        val url : URL = URL( "${apiUrl}lat=${lat}&lon=${lon}&appid=${apiKey}" )
        val iStream : InputStream = url.openStream()
        // read from iStream
        val scan : Scanner = Scanner( iStream )
        result = ""
        while( scan.hasNext( ) ) {
            result += scan.nextLine( )
        }
        Log.w( "MainActivity", "result is $result" )

//         update GUI
//            var updateGui : UpdateGui = UpdateGui( )
//            activity.runOnUiThread( updateGui )
    }

//        inner class UpdateGui : Runnable {
//            override fun run() {
//                Log.w( "MainActivity", "Inside UpdateGui:run" )
//                activity.updateView( result )
//            }
//        }

    companion object {
        const val apiKey : String = "0ba2ea1e33c83fe7e654c394952a1fe7"
        const val apiUrl : String = "https://api.openweathermap.org/data/2.5/weather?"
    }
}