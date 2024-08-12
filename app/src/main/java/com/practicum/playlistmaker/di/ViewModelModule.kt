package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.view_model.SearchTracksViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel <SearchTracksViewModel> {
        SearchTracksViewModel(trackInteractor = get())
    }

    viewModel <PlayerViewModel> {
        PlayerViewModel(playerInteractor = get())
    }
    viewModel<SettingsViewModel>() {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }
}