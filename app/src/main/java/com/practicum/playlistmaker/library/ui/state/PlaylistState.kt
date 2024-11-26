package com.practicum.playlistmaker.library.ui.state

import com.practicum.playlistmaker.library.domain.Playlist

sealed interface PlaylistState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    object Empty : PlaylistState
}