package com.practicum.playlistmaker.library.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTrackRepository {
    suspend fun likeTrack(track: Track)
    suspend fun unlikeTrack(track: Track)
    fun getTracks(): Flow<List<Track>>
}