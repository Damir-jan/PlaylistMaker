package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.domain.db.FavoritesTrackInteractor
import com.practicum.playlistmaker.library.domain.db.LocalStorageInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.impl.FavoritesTrackInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.LocalStorageInteractorImpl
import com.practicum.playlistmaker.library.domain.impl.PlaylistsInteractorImpl
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
        TrackInteractorImpl(repository = get())
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
    single<FavoritesTrackInteractor> {
        FavoritesTrackInteractorImpl(favoriteTrackRepository = get())
    }

    factory<LocalStorageInteractor> {
        LocalStorageInteractorImpl(localStorageRepository = get())
    }
    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(playlistsRepository = get())
    }
}
