package com.practicum.playlistmaker.settings.ui.view_model

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
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
        themeLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(isChecked)
        applyTheme(isChecked)
    }


    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}