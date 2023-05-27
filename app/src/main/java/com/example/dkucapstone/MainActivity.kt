package com.example.dkucapstone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rtugeek.android.colorseekbar.AlphaSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.util.Locale
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()


    private val PREF_NAME = "MyPreferences"
    private val KEY_PROGRESS = "progress"
    private var colorcode = "000000"
    private var alphaInt = 0


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        latitude = 37.54
        longitude = 126.0
        val context: Context = applicationContext
        val weatherApiClient = WeatherApiClient(context)





        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            return
        }


        // 위치 권한 요청

        requestLocationUpdates()



            val locationTxt: TextView = findViewById(R.id.Location)
            val tempview: TextView = findViewById(R.id.temperature)
            val weatherview: TextView = findViewById(R.id.weather)
            val settingBtn: View = findViewById<Button>(R.id.Settingbtn)
            val colorSeekBar: ColorSeekBar = findViewById(R.id.ColorSlide)
            val alphaSeekBar: AlphaSeekBar = findViewById(R.id.AlphaSlide)
            val refreshBtn: FloatingActionButton = findViewById(R.id.Refreshbtn)
            val saveButton: FloatingActionButton = findViewById(R.id.Maxpowerbtn)// customize btn
            val autoOptimizeButton: FloatingActionButton = findViewById(R.id.Optimizebtn)
            val autoButton: FloatingActionButton = findViewById(R.id.Autobtn)

            sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
            sharedPreferences2 = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

            // 이전에 저장된 값이 있는 경우, 해당 값을 SeekBar에 설정합니다.
            val savedProgress = sharedPreferences.getInt(KEY_PROGRESS, 0)
            val savedProgress2 = sharedPreferences.getInt(KEY_PROGRESS, 0)
            colorSeekBar.progress = savedProgress
            alphaSeekBar.progress = savedProgress2


            locationTxt.text = getCityName(latitude, longitude)

            autoOptimizeButton.setOnClickListener{
                weatherApiClient.getWeatherData(latitude.toString(),longitude.toString()) { response ->
                    if (response != null) {
                        // 응답이 성공적으로 도착한 경우
                        val temperature = response.getJSONObject("main").getDouble("temp")
                        val weatherDescription =
                            response.getJSONArray("weather").getJSONObject(0)
                                .getString("description")

                        // 사용자가 필요한 작업 수행
                        tempview.text = String.format("%.1f", temperature - 273.15)
                        weatherview.text = weatherDescription
                        println("Temperature: $temperature")
                        println("Weather description: $weatherDescription")
                    } else {
                        // 오류가 발생한 경우
                        println("Failed to get weather data.")
                    }
                }
                // weather refresh
                makeoptRequest(weatherview.text.toString())
            }

            saveButton.setOnClickListener{
                val data: String = colorcode
                var file_data = data
                while (file_data.length < 6) {
                    file_data = "0$file_data"
                }
                file_data = colorcodeToRGB(file_data,alphaInt)
                saveToFile(applicationContext, file_data)
                makeRequest(colorcode)
            }


            colorSeekBar.setOnColorChangeListener{
                progress, color ->
                colorcode = String.format("%x", 0xffffff and color)
                Log.i("TAG", "===progress:$progress-color:$color==")
                //where to use the color and use the color, ex) sample.setBackgroundColor(color)
            }
            alphaSeekBar.setOnAlphaChangeListener{
                progress, alpha ->
                alphaInt = getAlphaInt(alpha)
                Log.i("AlphaSeekBarFragment", "===progress:$progress-alpha:$alphaInt===")
            }
            refreshBtn.setOnClickListener{
                requestLocationUpdates()
                locationTxt.text = getCityName(latitude, longitude)
                weatherApiClient.getWeatherData(latitude.toString(), longitude.toString()) { response ->
                    if (response != null) {
                        // 응답이 성공적으로 도착한 경우
                        val temperature = response.getJSONObject("main").getDouble("temp")
                        val weatherDescription =
                            response.getJSONArray("weather").getJSONObject(0)
                                .getString("description")

                        // 사용자가 필요한 작업 수행
                        tempview.text = String.format("%.1f", temperature - 273.15)
                        weatherview.text = weatherDescription
                        println("Temperature: $temperature")
                        println("Weather description: $weatherDescription")
                    } else {
                        // 오류가 발생한 경우
                        println("Failed to get weather data.")
                    }
                }



            }
            autoButton.setOnClickListener{
                makeoffRequest()
            }
        }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    private fun getCityName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses :List<Address> = geocoder.getFromLocation(latitude, longitude,1) as List<Address>
        val cityName = if (addresses.isNotEmpty()) {
            addresses[0].locality ?: addresses[0].subAdminArea ?: ""
        } else {
            ""
        }
        return cityName
    }
    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // 위치 정보를 사용하여 작업 수행
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    println("위도: $latitude, 경도: $longitude")
                    // 위치 정보 사용 예시:
                    // 사용자의 현재 위치에 대한 작업 수행
                    // 예: 지도에 마커 표시 등
                }
                else{
                    latitude = 37.9
                    longitude = 127.0
                    println("위도: $latitude, 경도: $longitude")
                }
            }
            .addOnFailureListener { exception: Exception ->
                println("error loc")
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용된 경우 위치 정보 확인
                requestLocationUpdates()
            } else {
                // 위치 권한이 거부된 경우 예외 처리
            }
        }
    }




    private fun getAlphaInt(alpha: Int): Int {
        return alpha
    }


    private fun saveToFile(context: Context, file_text: String) {
        val fileName = "progress.txt"

        context.openFileOutput(fileName, MODE_PRIVATE).use {
            it.write(file_text.toByteArray())
        }

    }

    private fun weather_lightcon(weather:String) : String{
        return if(weather.contains("rain")or weather.contains("snow")){
            Log.d("weather","rain or snow")
            "204204204"
        }else if(weather.contains("cloud")){
            Log.d("weather","cloud")
            "128128128"
        } else{
            Log.d("weather","clear")
            "505050"
        }
    }
    private fun makeoptRequest(str: String) {
        val requestQueue =
            Volley.newRequestQueue(applicationContext) // context는 액티비티나 애플리케이션의 context입니다.
        val baseurl: String = "http://172.30.1.67/gpio1/"
        val rgb = weather_lightcon(str)
        val url: String = "$baseurl$rgb"
        println(url)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // 응답을 받았을 때 처리할 로직을 작성합니다.
                // response 변수에는 서버의 응답이 담겨 있습니다.
                println("응답: $response")
            },
            { error ->
                // 에러가 발생했을 때 처리할 로직을 작성합니다.
                // error 변수에는 발생한 에러 정보가 담겨 있습니다.
                println("에러: ${error.message}")
            })

        requestQueue.add(stringRequest)
    }

    private fun makeoffRequest() {
        val requestQueue =
            Volley.newRequestQueue(applicationContext) // context는 액티비티나 애플리케이션의 context입니다.
        val baseurl: String = "http://172.30.1.67/gpio1/0"
        println(baseurl)
        val stringRequest = StringRequest(
            Request.Method.GET, baseurl,
            { response ->
                // 응답을 받았을 때 처리할 로직을 작성합니다.
                // response 변수에는 서버의 응답이 담겨 있습니다.
                println("응답: $response")
            },
            { error ->
                // 에러가 발생했을 때 처리할 로직을 작성합니다.
                // error 변수에는 발생한 에러 정보가 담겨 있습니다.
                println("에러: ${error.message}")
            })

        requestQueue.add(stringRequest)
    }

    private fun makeRequest(str: String) {
        val requestQueue =
            Volley.newRequestQueue(applicationContext) // context는 액티비티나 애플리케이션의 context입니다.
        val baseurl: String = "http://172.30.1.67/gpio1/"
        val rgb = readTextFile("${applicationContext.filesDir}/progress.txt")
        val url: String = "$baseurl$rgb"
        println(url)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // 응답을 받았을 때 처리할 로직을 작성합니다.
                // response 변수에는 서버의 응답이 담겨 있습니다.
                println("응답: $response")
            },
            { error ->
                // 에러가 발생했을 때 처리할 로직을 작성합니다.
                // error 변수에는 발생한 에러 정보가 담겨 있습니다.
                println("에러: ${error.message}")
            })

        requestQueue.add(stringRequest)
    }

    private fun readTextFile(fullPath: String): String {

        val file = File(fullPath)  // 파일 생성
        if (!file.exists()) return ""  // 실제 파일이 있는지 검사

        val reader = FileReader(file)  // FileReader로 파일을 읽고
        val buffer = BufferedReader(reader)  // BufferedReader에 담아서 속도 향상

        var temp: String? = null  // buffer를 통해 한 줄씩 읽은 내용을 저장할 temp
        var result = ""  // 모든 내용을 저장할 result

        while (true) {
            temp = buffer.readLine()  // 버퍼에서 한 줄을 꺼내 temp에 담고
            if (temp == null) break  // 내용이 더 이상 없으면 빠져나가고,
            else result += temp + "\n"  // 값이 있다면 result 변수에 추가

        }

        buffer.close()  // buffer 닫기
        reader.close()  // reader 닫기

        return result
    }

    private fun colorcodeToRGB(code : String, alpha: Int) : String{
        val rv : Int
        val gv : Int
        val bv : Int
        val ratio = (256 - alpha) / 256.0
        var red : String
        var green : String
        var blue : String
        if(code.length != 6){
            Log.d("fun","invaild color code")
        }
        else{
            rv = code.substring(0,2).toInt(16)
            gv = code.substring(2,4).toInt(16)
            bv = code.substring(4,).toInt(16)

            red = (rv*ratio).toInt().toString()
            green = (gv*ratio).toInt().toString()
            blue = (bv*ratio).toInt().toString()

            while (red.length <3){
                red = "0$red"
            }
            while (green.length <3){
                green = "0$green"
            }
            while (blue.length <3){
                blue = "0$blue"
            }
            return "$red$green$blue"
        }
        return ""
    }
}






