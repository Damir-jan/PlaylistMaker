package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.Resource
import com.practicum.playlistmaker.search.domain.models.Track


interface SearchRepository {

    fun searchTracks(text: String): Resource<List<Track>>
    fun saveTrackToHistory(track: List<Track>)
    fun readTracksFromHistory(): Array<Track>
    fun clearHistory()
}