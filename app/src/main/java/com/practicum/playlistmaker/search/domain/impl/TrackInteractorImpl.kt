package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.Resource
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: SearchRepository) : TrackInteractor {

    override fun searchTracks(text: String) : Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(text).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun saveTrackToHistory(track: List<Track>) {
        repository.saveTrackToHistory(track)
    }

    override fun readTracksFromHistory(): List<Track> {
        return repository.readTracksFromHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}