package com.practicum.playlistmaker.player.ui.models

sealed interface PlayerState {
    object Prepare : PlayerState
    object Play : PlayerState
    object Pause : PlayerState

    data class UpdatePlayingTime(val time: String) : PlayerState
}