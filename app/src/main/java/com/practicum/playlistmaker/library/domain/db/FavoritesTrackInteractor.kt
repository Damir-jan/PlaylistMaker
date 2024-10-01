package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTrackInteractor {
    suspend fun likeTrack(track: Track)   //???? что-то должен возвращать?
    suspend fun unlikeTrack(track: Track) //???? что-то должен возвращать?
    fun getTracks(): Flow<List<Track>>
}