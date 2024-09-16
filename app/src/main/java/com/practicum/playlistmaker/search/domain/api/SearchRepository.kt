package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.Resource
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface SearchRepository {

    fun searchTracks(text: String): Flow<Resource<List<Track>>>
    fun saveTrackToHistory(track: List<Track>)
    fun readTracksFromHistory(): List<Track>
    fun clearHistory()
}