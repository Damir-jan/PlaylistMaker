package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmaker.library.data.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmaker.library.data.converter.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.repository.PlaylistsRepositoty
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepositoty {
    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: ArrayList<Int>,
        track: Track
    ) {
        appDatabase.getTrackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        tracksId.add(track.trackId)
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = tracksId,
            tracksCount = playlist.tracksCount + 1
        )
        appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getSavedPlaylists().map {
            it.map { entity -> entity.toPlaylist() }
        }
    }
    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist.toPlaylistEntity())
    }
}