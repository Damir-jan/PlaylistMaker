package com.practicum.playlistmaker

import Track
import android.content.SharedPreferences
import com.google.gson.Gson


const val HISTORY_TRACKS_LIST_KEY = "history_tracks_list"
const val quantityTracksInHistory = 10


class SearchHistory(val sharedPreferences: SharedPreferences) {
    fun saveTrack(track: MutableList<Track>) {
        val historyTracks = readTracks().toMutableList()

        historyTracks.removeAll { existingTrack ->
            track.any { it.trackId == existingTrack.trackId }
        }

        historyTracks.addAll(0, track)

        if (historyTracks.size > quantityTracksInHistory) {
            historyTracks.removeAt(quantityTracksInHistory)
        }

        val json = Gson().toJson(historyTracks)
        sharedPreferences.edit()
            .putString(HISTORY_TRACKS_LIST_KEY, json)
            .apply()
    }

    fun readTracks(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_TRACKS_LIST_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }
}