package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.view_model.SearchTracksViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel <SearchTracksViewModel> {
        SearchTracksViewModel(trackInteractor = get())
    }

    viewModel <PlayerViewModel> {
        PlayerViewModel(playerInteractor = get(), favoritesTrackInteractor = get(), playlistsInteractor = get())

    }
    viewModel<SettingsViewModel>() {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }

    viewModel {
        FavoriteTracksViewModel(context = androidContext(), favoritesTrackInteractor = get())
    }

    viewModel {
        NewPlaylistViewModel(localStorageInteractor = get(), playlistsInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistsInteractor = get())
    }
}