package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

const val NIGHT_THEME_KEY = "night_theme"

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefs.getBoolean(NIGHT_THEME_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit()
            .putBoolean(NIGHT_THEME_KEY, settings.darkTheme)
            .apply()
    }

}