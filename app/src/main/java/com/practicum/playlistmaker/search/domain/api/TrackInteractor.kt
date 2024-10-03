package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(text: String) : Flow<Pair<List<Track>?, String?>>

    suspend fun saveTrackToHistory(track: List<Track>)

    suspend fun readTracksFromHistory(): List<Track>

    fun clearHistory()
}