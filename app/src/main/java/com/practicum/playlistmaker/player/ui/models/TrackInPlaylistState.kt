package com.practicum.playlistmaker.player.ui.models

sealed interface TrackInPlaylistState {
    object Idle : TrackInPlaylistState
    data class TrackIsAlreadyInPlaylist(val playlistName: String) : TrackInPlaylistState
    data class TrackAddToPlaylist(val playlistName: String) : TrackInPlaylistState

}