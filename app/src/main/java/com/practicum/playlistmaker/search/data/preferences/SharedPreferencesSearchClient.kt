package com.practicum.playlistmaker.search.data.preferences

import com.practicum.playlistmaker.search.domain.models.Track

interface SharedPreferencesSearchClient {
    fun saveTrackToHistory(track: List<Track>)
    fun readTracksFromHistory(): Array<Track>
    fun clearHistory()
}