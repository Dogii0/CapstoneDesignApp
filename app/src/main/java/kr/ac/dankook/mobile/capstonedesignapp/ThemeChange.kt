package kr.ac.dankook.mobile.capstonedesignapp

import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.*
import androidx.appcompat.app.AppCompatActivity

abstract class ThemeChange  : AppCompatActivity(){
    private var currentTheme = Blue

    override fun onCreate(savedInstanceState: Bundle?) {
        currentTheme = getDefaultSharedPreferences(this).getInt(KEY_THEME, Blue)
        super.onCreate(savedInstanceState)
    }

    protected fun setTheme() { setTheme(currentTheme) }
    protected fun switchTheme() {
        currentTheme = when(currentTheme) {
            Blue -> White
            White -> Blue
            else -> -1
        }

        getDefaultSharedPreferences(this).edit().putInt(KEY_THEME, currentTheme).apply()
    }

    companion object {
        private const val KEY_THEME = "Theme"
        private const val Blue = R.style.BlueTheme
        private const val White = R.style.WhiteTheme
    }