package com.example.dkucapstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SettingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_page)

        val backBtn : View = findViewById<Button>(R.id.ReturnBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}