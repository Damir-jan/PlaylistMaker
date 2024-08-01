package com.practicum.playlistmaker.search.data.preferences

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.practicum.playlistmaker.search.domain.models.Track

const val HISTORY_TRACKS_LIST_KEY = "history_tracks_list"
const val maxCountOfTracksInHistory = 10

class SharedPreferencesSearchClientImpl(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesSearchClient {

    override fun saveTrackToHistory(track: List<Track>) {

        val historyTracks = readTracksFromHistory().toMutableList()

        historyTracks.removeAll { existingTrack ->
            track.any { it.trackId == existingTrack.trackId }
        }

        historyTracks.addAll(0, track)

        if (historyTracks.size > maxCountOfTracksInHistory) {
            historyTracks.removeAt(maxCountOfTracksInHistory)
        }

        val json = Gson().toJson(historyTracks)
        sharedPreferences.edit()
            .putString(HISTORY_TRACKS_LIST_KEY, json)
            .apply()
    }

    override fun readTracksFromHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_TRACKS_LIST_KEY, null) ?: return emptyArray()
        Log.d("Test", "Читаю трек")
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(HISTORY_TRACKS_LIST_KEY)
            .apply()
    }
}