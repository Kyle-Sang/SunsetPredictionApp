package com.example.sunsetprediction

class WeatherData {
    var latitude : Float = 0f
    var longitude : Float = 0f
    var humidity : Float = 0f
    var temperature : Float = 0f
    var clouds : Float = 0f
    var pressure : Float = 0f
    var location : String = ""

    constructor(latitude: Float, longitude : Float) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateParams(newHumidity : Float, newTemperature : Float, newClouds : Float, newPressure : Float, newLocation : String) {
        this.humidity = newHumidity
        this.temperature = newTemperature
        this.clouds = newClouds
        this.pressure = newPressure
        this.location = newLocation
    }
}