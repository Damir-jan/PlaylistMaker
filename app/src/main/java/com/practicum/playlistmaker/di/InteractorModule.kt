package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.InteractorImpl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.InteractorImpl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import org.koin.dsl.module

val interactorModule = module {
    factory<TrackInteractor> {
        TrackInteractorImpl(repository = get(),executor = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(playerRepository = get())
    }

    factory<SettingsInteractor> {

        SettingsInteractorImpl(settingsRepository = get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
}
