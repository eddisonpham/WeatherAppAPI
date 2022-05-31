package com.example.weatherapp

class WeatherRVModal {

    var time:String
        get(){
            return time
        }
        set(value){
            this.time=value
        }
    var temperature:String
        get(){
            return temperature
        }
        set(value){
            this.temperature=value
        }
    var icon:String
        get(){
            return icon
        }
        set(value){
            this.icon=value
        }
    var windSpeed:String
        get(){
            return windSpeed
        }
        set(value){
            this.windSpeed=value
        }
    constructor(time: String, temperature: String, icon: String, windSpeed: String) {
        this.time = time
        this.temperature = temperature
        this.icon = icon
        this.windSpeed = windSpeed
    }

}