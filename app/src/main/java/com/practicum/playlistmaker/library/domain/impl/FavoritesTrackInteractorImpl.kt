package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.db.FavoritesTrackInteractor
import com.practicum.playlistmaker.library.domain.repository.FavoritesTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesTrackInteractorImpl(private val favoriteTrackRepository: FavoritesTrackRepository) :
    FavoritesTrackInteractor {
    override suspend fun likeTrack(track: Track) {
        favoriteTrackRepository.likeTrack(track)
    }

    override suspend fun unlikeTrack(track: Track) {
        favoriteTrackRepository.unlikeTrack(track)
    }

    override fun getTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getTracks()
    }

}