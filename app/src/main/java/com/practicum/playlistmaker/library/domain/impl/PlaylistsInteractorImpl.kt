package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.repository.PlaylistsRepositoty
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepositoty) :
    PlaylistsInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun addTracksIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track
    ) {
        playlistsRepository.addTrackIdInPlaylist(playlist, tracksId as ArrayList<Int>, track)
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getSavedPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override fun getTracksInPlaylistsById(tracksIdInPlaylist: List<Int>): Flow<List<Track>> {
        return playlistsRepository.getTracksInPlaylistsById(tracksIdInPlaylist)

    }

    override suspend fun deleteTrackInPlaylist(playlist: Playlist, trackId: Int) {
        playlistsRepository.deleteTrackInPlaylist(playlist, trackId)
    }
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }
}