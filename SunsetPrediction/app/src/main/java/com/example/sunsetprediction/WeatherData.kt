package com.example.sunsetprediction

class WeatherData {
    private var latitude : Float = 0f
    private var longitude : Float = 0f
    private var humidity : Float = 0f
    private var temperature : Float = 0f
    private var location : String = ""

    constructor(latitude: Float, longitude : Float) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateParams() {
        // calls weather API
        this.humidity = humidity // replace this with the data from API
        this.temperature = temperature
        this.location = location
    }
}