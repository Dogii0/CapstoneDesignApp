package kr.ac.dankook.mobile.capstonedesignapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rtugeek.android.colorseekbar.AlphaSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
            } // if this keeps on giving null argument, use getCurrentLocation

        val locationTxt:View = findViewById(R.id.Location)
        val settingBtn : View = findViewById<Button>(R.id.Settingbtn)
        val colorSeekBar: ColorSeekBar = findViewById(R.id.ColorSlide)
        val alphaSeekBar: AlphaSeekBar = findViewById(R.id.AlphaSlide)
        val refreshBtn: View = findViewById(R.id.refreshBtn)

        settingBtn.setOnClickListener {
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }

        colorSeekBar.setOnColorChangeListener { progress, color ->
            Log.i("TAG", "===progress:$progress-color:$color===")
            //where to use the color and use the color, ex) sample.setBackgroundColor(color)
        }
        alphaSeekBar.setOnAlphaChangeListener { progress, alpha ->
            Log.i("AlphaSeekBarFragment", "===progress:$progress-alpha:$alpha===")
        }
    }
}