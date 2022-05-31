package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import okhttp3.Address
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var homeRl:RelativeLayout
    private lateinit var loadingPB:ProgressBar
    private lateinit var cityNameTV:TextView
    private lateinit var temperatureTV:TextView
    private lateinit var conditionTV:TextView
    private lateinit var cityEdt:TextInputEditText
    private lateinit var iconIV:ImageView
    private lateinit var backIV:ImageView
    private lateinit var searchIV:ImageView
    private lateinit var weatherRV:RecyclerView
    private lateinit var weatherRVModalArrayList:ArrayList<WeatherRVModal>
    private lateinit var weatherRVAdapter:WeatherRVAdapter
    private lateinit var locationManager:LocationManager
    private var PERMISSION_CODE = 1
    private lateinit var cityName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)
        homeRl=findViewById(R.id.idRLHome)
        loadingPB=findViewById(R.id.idPBLocating)
        cityNameTV=findViewById(R.id.idTVCityName)
        temperatureTV=findViewById(R.id.idTVTemperature)
        conditionTV=findViewById(R.id.idTVCondition)
        cityEdt=findViewById(R.id.idEdtCity)
        iconIV=findViewById(R.id.idTVIcon)
        backIV=findViewById(R.id.idIVBack)
        searchIV=findViewById(R.id.idIVSearch)
        weatherRV=findViewById(R.id.idRVWeather)
        weatherRVModalArrayList= ArrayList()
        weatherRVAdapter= WeatherRVAdapter(this,weatherRVModalArrayList)
        weatherRV.adapter=weatherRVAdapter

        locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this,permission,PERMISSION_CODE)
        }
        var location = locationManager.getLastKnownLocation((LocationManager.NETWORK_PROVIDER))
        cityName = getCityName(location!!.longitude, location!!.latitude)
        getWeatherInfo(cityName)

        searchIV.setOnClickListener{
            var city:String = cityEdt.text.toString()
            if (city.isEmpty()){
                Toast.makeText(this, "Please enter city Name", Toast.LENGTH_SHORT).show()
            }else{
                cityNameTV.setText(cityName)
                getWeatherInfo(city)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION_CODE){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun getCityName (longitude:Double, latitude:Double):String{
        var cityName = "Not found"
        var gcd:Geocoder = Geocoder(getBaseContext(), Locale.getDefault())
        try{
            var addresses:List<android.location.Address> = gcd.getFromLocation(latitude,longitude,10)
            for (adr:android.location.Address in addresses){
                if (adr!=null){
                    var city:String = adr.locality
                    if(city!=null && !city.equals("")){
                        cityName = city
                    }else{
                        Log.d("TAG", "CITY NOT FOUND")
                        Toast.makeText(this,"User City Not Found...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
        return cityName
    }
    private fun getWeatherInfo(cityName:String){
        var url:String="https://api.weatherapi.com/v1/forecast.json?key=0971e1ec25da4ddebb012819223005&q="+cityName+"&days=1&aqi=no&alerts=no"
        cityNameTV.setText(cityName)
        var requestQueue: RequestQueue = Volley.newRequestQueue(this)
        var jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener{ response->
                loadingPB.isGone=true
                homeRl.isVisible=true
                weatherRVModalArrayList.clear()

                var temperature:String = response.getJSONObject("current").getString("temp_c")
                temperatureTV.setText(temperature+"°c")
                var isDay:Int = response.getJSONObject("current").getInt("is_day")
                var condition:String = response.getJSONObject("current").getJSONObject("condtion").getString("text")
                var conditionIcon:String = response.getJSONObject("current").getJSONObject("condtion").getString("icon")
                Picasso.get().load("http:"+conditionIcon).into(iconIV)
                conditionTV.setText(condition)
                if(isDay==1){
                    Picasso.get().load("https://images.unsplash.com/photo-1513002749550-c59d786b8e6c?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8c2t5fGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=600&q=60").into(backIV)
                }else{
                    Picasso.get().load("https://images.unsplash.com/photo-1505322022379-7c3353ee6291?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV)
                }

                var forecast:JSONObject = response.getJSONObject("forecast")
                var forecastday:JSONObject = forecast.getJSONArray("forecastday").getJSONObject(0)
                var hourArray:JSONArray=forecastday.getJSONArray("hour")
                for(i in 0 until hourArray.length()){
                    var hourObj:JSONObject=hourArray.getJSONObject(i)
                    var time:String = hourObj.getString("time")
                    var temperature:String = hourObj.getString("temp_c")
                    var img:String = hourObj.getJSONObject("condition").getString("icon")
                    var wind:String = hourObj.getString("wind_kph")
                    weatherRVModalArrayList.add(WeatherRVModal(time,temperature,img,wind))
                }
                weatherRVAdapter.notifyDataSetChanged()
            },Response.ErrorListener { error->
                Toast.makeText(this, "Please enter valid city name...", Toast.LENGTH_SHORT).show()
            })
            requestQueue.add(jsonObjectRequest)
    }
}