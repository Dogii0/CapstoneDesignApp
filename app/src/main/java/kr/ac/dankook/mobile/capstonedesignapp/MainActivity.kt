package kr.ac.dankook.mobile.capstonedesignapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.rtugeek.android.colorseekbar.AlphaSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar.*
import com.rtugeek.android.colorseekbar.OnColorChangeListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingBtn : View = findViewById<Button>(R.id.Settingbtn)
        val colorSeekBar: ColorSeekBar = findViewById(R.id.ColorSlide)
        val alphaSeekBar: AlphaSeekBar = findViewById(R.id.AlphaSlide)
        val test: View = findViewById(R.id.view)

        settingBtn.setOnClickListener {
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }

        colorSeekBar.setOnColorChangeListener { progress, color ->
            Log.i("TAG", "===progress:$progress-color:$color===")
            test.setBackgroundColor(color)
        }
        alphaSeekBar.setOnAlphaChangeListener { progress, alpha ->
            Log.i("AlphaSeekBarFragment", "===progress:$progress-alpha:$alpha===")
        }
    }
}