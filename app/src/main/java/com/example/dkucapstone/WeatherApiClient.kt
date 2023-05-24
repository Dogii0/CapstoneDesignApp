package com.example.dkucapstone

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class WeatherApiClient(context: Context) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val apiKey = "ff45ac64b4ab396fcc8c7e96e5d4c627"
    private val baseUrl = "https://api.openweathermap.org/data/2.5/weather"

    fun getWeatherData(lat: String,lon : String, callback: (JSONObject?) -> Unit) {
        val url = "$baseUrl?lat=$lat&lon=$lon&appid=$apiKey"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                callback(response)
            },
            Response.ErrorListener { error ->
                // error handle
                callback(null)
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}