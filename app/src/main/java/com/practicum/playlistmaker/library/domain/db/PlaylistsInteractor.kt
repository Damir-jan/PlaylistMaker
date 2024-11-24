package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updateIdTracksInPlaylist(playlist: Playlist, tracksId: List<Int>)
    fun getSavedPlaylists(): Flow<List<Playlist>>
}