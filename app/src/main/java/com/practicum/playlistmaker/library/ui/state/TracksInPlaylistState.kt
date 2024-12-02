package com.practicum.playlistmaker.library.ui.state

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TracksInPlaylistState {
    data class Content(
        val tracksInPlaylist: List<Track>
    ) : TracksInPlaylistState

    object Empty : TracksInPlaylistState
}