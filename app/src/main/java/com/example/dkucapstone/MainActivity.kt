package com.example.dkucapstone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rtugeek.android.colorseekbar.AlphaSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private val PREF_NAME = "MyPreferences"
    private val KEY_PROGRESS = "progress"
    private var colorcode = 0
    private var alphaInt = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context: Context = applicationContext
        val weatherApiClient = WeatherApiClient(context)




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {


        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        val locationTxt:View = findViewById(R.id.Location)
        val tempview: TextView = findViewById(R.id.temperature)
        val weatherview : TextView = findViewById(R.id.weather)
        val settingBtn : View = findViewById<Button>(R.id.Settingbtn)
        val colorSeekBar: ColorSeekBar = findViewById(R.id.ColorSlide)
        val alphaSeekBar: AlphaSeekBar = findViewById(R.id.AlphaSlide)
        val refreshBtn: FloatingActionButton = findViewById(R.id.Refreshbtn)
        val saveButton: FloatingActionButton= findViewById(R.id.Optimizebtn)//
        val autoButton : FloatingActionButton = findViewById(R.id.Autobtn)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences2 = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 이전에 저장된 값이 있는 경우, 해당 값을 SeekBar에 설정합니다.
        val savedProgress = sharedPreferences.getInt(KEY_PROGRESS, 0)
        val savedProgress2 = sharedPreferences.getInt(KEY_PROGRESS, 0)
        colorSeekBar.progress = savedProgress
        alphaSeekBar.progress = savedProgress2


        saveButton.setOnClickListener {
            val hexString = Integer.toHexString(colorcode)
            val data : String = "$hexString/$alphaInt"
            saveToFile(applicationContext,data)
        }

        settingBtn.setOnClickListener {
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }

        colorSeekBar.setOnColorChangeListener { progress, color ->
            colorcode = 0xffffff and color
            Log.i("TAG", "===progress:$progress-color:$color==")
            //where to use the color and use the color, ex) sample.setBackgroundColor(color)
        }
        alphaSeekBar.setOnAlphaChangeListener { progress, alpha ->
            alphaInt = getAlphaInt(alpha)
            Log.i("AlphaSeekBarFragment", "===progress:$progress-alpha:$alpha===")
        }
        refreshBtn.setOnClickListener {

            weatherApiClient.getWeatherData("37.54","127") { response ->
                        if (response != null) {
                            // 응답이 성공적으로 도착한 경우
                            val temperature = response.getJSONObject("main").getDouble("temp")
                            val weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description")

                            // 사용자가 필요한 작업 수행
                            tempview.text = String.format("%.1f", temperature-273.15)
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
            makeRequest(Integer.toHexString(colorcode))
        }
    }
    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
    }
    fun getColorCode(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    private  fun getAlphaInt(alpha: Int): Int {
        return alpha
    }


    private fun getLocation(callback: LocationCallback) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    callback.onLocationReceived(latitude, longitude)
                }
            }
            .addOnFailureListener { exception ->
                exception?.let { Log.i("TAG", "===progress: err ===") }
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation(object : LocationCallback {
                    override fun onLocationReceived(latitude: Double, longitude: Double) {
                        // 위치 정보를 받은 후에 실행될 코드 작성
                        // 예: 위치 정보를 활용한 작업 수행
                    }
                })
            } else {
                // 위치 권한이 거부된 경우 예외 처리 코드 작성
            }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    private fun saveToFile(context: Context,text: String) {
        val fileName = "progress.txt"
        val fileContents = text
        val outputStream: FileOutputStream
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
        println("$text success")




            //저장 성공

    }


    private fun makeRequest(str : String) {
        val requestQueue = Volley.newRequestQueue(applicationContext) // context는 액티비티나 애플리케이션의 context입니다.
        val baseurl : String = "http://172.30.1.61/gpio1/"
        val rgb = readProgressFromFile("progress.txt")
        val url : String = "$baseurl$str"
        println(url)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                // 응답을 받았을 때 처리할 로직을 작성합니다.
                // response 변수에는 서버의 응답이 담겨 있습니다.
                println("응답: $response")
            },
            Response.ErrorListener { error ->
                // 에러가 발생했을 때 처리할 로직을 작성합니다.
                // error 변수에는 발생한 에러 정보가 담겨 있습니다.
                println("에러: ${error.message}")
            })

        requestQueue.add(stringRequest)
    }
     private fun readProgressFromFile(filePath: String): String {
         try {
             val fis = FileInputStream(filePath)
             val buffer = ByteArray(1024)
             var bytesRead: Int
             var result : String


             while (fis.read(buffer).also { bytesRead = it } != -1) {
                 // 읽은 데이터를 처리하거나 출력하는 작업을 수행합니다.
                 val data = buffer.copyOf(bytesRead)
                 val text = String(data)
                 println(text)
                 val progress = text
                 val substr = progress.split("/")
                 val R_val = substr[0].substring(0,2).toInt(16)
                 val B_val = substr[0].substring(2,4).toInt(16)
                 val G_val = substr[0].substring(4,).toInt(16)
                 val alpha_v = substr[1].toInt()
                 val total = R_val + B_val + G_val
                 val ratioR= R_val.toDouble() / total
                 val ratioB = B_val.toDouble() / total
                 val ratioG = G_val.toDouble() / total
                 return "$R_val$B_val$G_val"

             }

             fis.close()

         } catch (e: Exception) {
             e.printStackTrace()
         }

      return "0"// 기본값
    }




}





