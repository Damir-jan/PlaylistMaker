package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTrackInteractor {
    suspend fun likeTrack(track: Track)
    suspend fun unlikeTrack(track: Track)
    fun getTracks(): Flow<List<Track>>

    suspend fun isTrackFavorite(trackId: Int): Boolean
}