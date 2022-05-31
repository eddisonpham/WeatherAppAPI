package com.example.weatherapp

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherRVAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private lateinit var context: Context
    private lateinit var weatherRVModalArrayList:ArrayList<WeatherRVModal>

    constructor(context: Context, weatherRVModalArrayList: ArrayList<WeatherRVModal>):super() {
        this.context = context
        this.weatherRVModalArrayList = weatherRVModalArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var modal = weatherRVModalArrayList.get(position)
        (holder as ViewHolder).temperatureTV.setText(modal.temperature+"°c")
        Picasso.get().load("http:"+modal.icon).into(holder.conditionIV)
        (holder as ViewHolder).windTV.setText(modal.windSpeed+"Km/h")
        var input:SimpleDateFormat= SimpleDateFormat("yyyy-MM-dd hh:mm")
        var output:SimpleDateFormat=SimpleDateFormat("hh:mm aa")
        try{
            var t: Date = input.parse(modal.time)
            (holder as ViewHolder).timeTV.setText(output.format(t))
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return weatherRVModalArrayList.size
    }
    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var windTV = itemView.findViewById<TextView>(R.id.idTVWindSpeed)
        var temperatureTV = itemView.findViewById<TextView>(R.id.idTVTemperature)
        var timeTV = itemView.findViewById<TextView>(R.id.idTVTime)
        var conditionIV = itemView.findViewById<ImageView>(R.id.idTVCondition)
    }


}