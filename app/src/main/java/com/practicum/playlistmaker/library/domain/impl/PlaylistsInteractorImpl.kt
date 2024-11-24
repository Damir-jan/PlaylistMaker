package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.repository.PlaylistsRepositoty
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepositoty) :
    PlaylistsInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun updateIdTracksInPlaylist(playlist: Playlist, tracksId: List<Int>) {
        playlistsRepository.updateIdTracksInPlaylist(playlist, tracksId)
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getSavedPlaylists()
    }
}