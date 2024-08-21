package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

const val PLAYLIST_MAKER_THEME = "playlist_maker_theme_preferences"


class App : Application() {

    private val sharedPrefs by lazy {
        getSharedPreferences(PLAYLIST_MAKER_THEME, MODE_PRIVATE)
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(dataModule, repositoryModule, interactorModule, viewModelModule))
        }
        val settingsInteractor : SettingsInteractor by inject()

        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        switchTheme(darkTheme)
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