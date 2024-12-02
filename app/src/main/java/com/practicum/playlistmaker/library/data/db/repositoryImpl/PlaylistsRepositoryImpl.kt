package com.practicum.playlistmaker.library.data.db.repositoryImpl

import com.practicum.playlistmaker.library.data.converter.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmaker.library.data.converter.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmaker.library.data.converter.TrackInPlaylistDbConvertor.toTrack
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

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return appDatabase.getPlaylistDao().getPlaylistById(playlistId)
            .map { entity -> entity?.toPlaylist() }
    }

    override fun getTracksInPlaylistsById(tracksIdInPlaylist: List<Int>): Flow<List<Track>> {
        return appDatabase.getTrackInPlaylistDao().getTracksInPlaylistsById(tracksIdInPlaylist)
            .map {
                it.map { entity -> entity.toTrack() }
            }
    }

    override suspend fun deleteTrackInPlaylist(playlist: Playlist, trackId: Int) {
        playlist.tracksIdInPlaylist.forEach { item ->
            if (trackId == item) {
                val updatedTracksId = playlist.tracksIdInPlaylist - trackId
                val updatedPlaylist = playlist.copy(
                    tracksIdInPlaylist = updatedTracksId,
                    tracksCount = playlist.tracksCount - 1
                )
                appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
                checkIsTrackLast(trackId)
            }
        }
    }

    private suspend fun checkIsTrackLast(trackId: Int) {
        val playlists = appDatabase.getPlaylistDao().getPlaylists()

        val isTrackInAnyPlaylist = playlists.any { playlist ->
            playlist.toPlaylist().tracksIdInPlaylist.contains(trackId)
        }

        if (!isTrackInAnyPlaylist) {
            appDatabase.getTrackInPlaylistDao().deleteTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().deletePlaylist(playlist.toPlaylistEntity())
        val trackIdInPlaylist = playlist.tracksIdInPlaylist
        trackIdInPlaylist.forEach { trackId -> checkIsTrackLast(trackId) }
    }

}