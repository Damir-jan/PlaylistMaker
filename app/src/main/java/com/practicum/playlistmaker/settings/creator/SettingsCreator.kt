package com.practicum.playlistmaker.settings.creator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl

object SettingsCreator {

    const val THEME_PREFERENCES = "playlist_maker_theme_preferences"

    fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences(
                THEME_PREFERENCES,
                MODE_PRIVATE
            )
        )
    }

    fun providesettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}