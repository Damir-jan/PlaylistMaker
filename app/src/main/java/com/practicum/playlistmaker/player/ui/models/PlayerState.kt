package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlayerState {
    data class Prepare(val track: Track) : PlayerState
    object Play : PlayerState
    object Pause : PlayerState

    data class UpdatePlayingTime(val time: String) : PlayerState
}