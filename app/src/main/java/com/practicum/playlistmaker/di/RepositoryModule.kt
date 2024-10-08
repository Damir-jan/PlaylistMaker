package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.data.FavoritesTrackRepositoryImpl
import com.practicum.playlistmaker.library.data.converter.TrackDbConvertor
import com.practicum.playlistmaker.library.domain.repository.FavoritesTrackRepository
import com.practicum.playlistmaker.player.data.repositoryImpl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(networkClient = get(), sharedPreferencesSearchClient = get(), appDatabase = get())
    }
    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
    factory { TrackDbConvertor() }

    single<FavoritesTrackRepository> {
        FavoritesTrackRepositoryImpl(appDatabase = get(), trackDbConvertor = get())
    }
}