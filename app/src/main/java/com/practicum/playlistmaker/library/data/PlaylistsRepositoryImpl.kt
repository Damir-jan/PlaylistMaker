package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmaker.library.data.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.repository.PlaylistsRepositoty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepositoty {
    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun updateIdTracksInPlaylist(playlist: Playlist, tracksId: List<Int>) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist.toPlaylistEntity())
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getSavedPlaylists().map {
            it.map { entity -> entity.toPlaylist() }
        }
    }
}