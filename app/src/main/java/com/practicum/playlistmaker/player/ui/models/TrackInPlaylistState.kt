package com.practicum.playlistmaker.player.ui.models

sealed interface TrackInPlaylistState {
    data class TrackIsAlreadyInPlaylist(val playlistName: String) : TrackInPlaylistState
    data class TrackAddToPlaylist(val playlistName: String) : TrackInPlaylistState
}