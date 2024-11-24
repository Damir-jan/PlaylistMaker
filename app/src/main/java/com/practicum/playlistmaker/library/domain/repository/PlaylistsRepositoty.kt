package com.practicum.playlistmaker.library.domain.repository

import com.practicum.playlistmaker.library.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepositoty {
    suspend fun createPlaylist (playlist : Playlist)
    suspend fun updateIdTracksInPlaylist (playlist : Playlist, tracksId: List<Int>)
    fun getSavedPlaylists() : Flow<List<Playlist>>
}