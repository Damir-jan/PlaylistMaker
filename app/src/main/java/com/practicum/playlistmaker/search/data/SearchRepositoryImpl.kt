package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.Resource
import com.practicum.playlistmaker.search.NetworkClient
import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.data.dto.TrackRequest
import com.practicum.playlistmaker.search.data.preferences.SharedPreferencesSearchClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferencesSearchClient: SharedPreferencesSearchClient
) : SearchRepository {


    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(text))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Нет интернета"))
            }

            200 -> {
                emit(Resource.Success((response as SearchResponse).results.map {
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
                }))
            }

            else -> {

                emit(Resource.Error("Ошибка сервера"))
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