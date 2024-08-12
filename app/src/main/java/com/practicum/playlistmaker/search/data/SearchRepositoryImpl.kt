package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.Resource
import com.practicum.playlistmaker.search.NetworkClient
import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.data.dto.TrackRequest
import com.practicum.playlistmaker.search.data.preferences.SharedPreferencesSearchClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferencesSearchClient: SharedPreferencesSearchClient
) : SearchRepository {


    override fun searchTracks(text: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(text))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Нет интернета")
            }

            200 -> {
                Resource.Success((response as SearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {

                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun saveTrackToHistory(track: List<Track>) {
        sharedPreferencesSearchClient.saveTrackToHistory(track)
    }

    override fun readTracksFromHistory(): List<Track> {
        return sharedPreferencesSearchClient.readTracksFromHistory()
    }

    override fun clearHistory() {
        sharedPreferencesSearchClient.clearHistory()
    }
}