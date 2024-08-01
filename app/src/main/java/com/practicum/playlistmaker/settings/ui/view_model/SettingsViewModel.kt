package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.creator.SettingsCreator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.creator.SharingCreator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,

    ) : ViewModel() {

    private var themeLiveData = MutableLiveData<Boolean>()

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().darkTheme

    }

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData



    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application //////////
                    SettingsViewModel(
                        SharingCreator.provideSharingInteractor(application),
                        SettingsCreator.providesettingsInteractor(application)
                    )
                }
            }
    }

    fun clickShareApp() {
        sharingInteractor.shareApp()
    }

    fun clickOpenSupport() {
        sharingInteractor.openSupport()
    }

    fun clickOpenTerms() {
        sharingInteractor.openTerms()
    }

    fun clickSwitchTheme(isChecked: Boolean) {
        Log.d("TEST", "clickSwitchTheme")
        themeLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(isChecked)
        applyTheme(isChecked)
    }


    private fun applyTheme(darkThemeEnabled: Boolean) {
        Log.d("TEST", "applyTheme")
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}