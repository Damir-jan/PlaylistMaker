package com.practicum.playlistmaker.library.domain.repository

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepositoty {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackIdInPlaylist(playlist: Playlist, tracksId: ArrayList<Int>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylistById(playlistId: Int): Flow<Playlist?>
    fun getTracksInPlaylistsById(tracksIdInPlaylist : List<Int>) : Flow<List<Track>>
    suspend fun deleteTrackInPlaylist(playlist: Playlist, trackId: Int)
    suspend fun deletePlaylist(playlist: Playlist)
}