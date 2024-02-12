package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_THEME = "playlist_maker_theme_preferences"
const val NIGHT_THEM_KEY = "night_theme"


class App : Application() {

    private val sharedPrefs by lazy {
        getSharedPreferences(PLAYLIST_MAKER_THEME, MODE_PRIVATE)
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = getThemSharedPreferences()
        switchTheme(darkTheme)
    }

    private fun getThemSharedPreferences(): Boolean {
        return sharedPrefs.getBoolean(NIGHT_THEM_KEY, false)
    }

    fun saveThemeToSharedPreferences() {
        sharedPrefs.edit()
            .putBoolean(NIGHT_THEM_KEY, darkTheme)
            .apply()
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}