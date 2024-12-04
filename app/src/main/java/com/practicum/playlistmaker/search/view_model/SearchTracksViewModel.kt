package com.practicum.playlistmaker.search.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchTracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val trackInteractor: TrackInteractor,
) : ViewModel() {


    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()


    }


    private var searchDebounceJob: Job? = null

    private val stateLiveData = MutableLiveData<SearchTracksState>()
    fun observeState(): LiveData<SearchTracksState> = stateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        searchDebounceJob?.cancel()
    }


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTrack(changedText)
        }
    }
   /* fun searchDebounce(changedText: String) {
        if (changedText.isBlank()) {
            latestSearchText = ""
            loadHistory()
            return
        }
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTrack(changedText)
        }
    }*/

    fun searchTrack(newSearchText: String) {
        if (newSearchText.isBlank()
        ) {
            return
        }

        searchDebounceJob?.cancel()

        renderState(SearchTracksState.Loading)

        viewModelScope.launch {
            trackInteractor.searchTracks(newSearchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }

    }

    private fun processResult(
        foundTracks: List<Track>?,
        errorMessage: String?
    ) {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.clear()
            tracks.addAll(foundTracks)
        }
        Log.d("processResult", "foundTracks: $foundTracks, errorMessage: $errorMessage")

        when {
            errorMessage != null -> {
                renderState(
                    SearchTracksState.Error(
                        errorMessage
                    )
                )
            }

            foundTracks?.isEmpty() == true -> {
                renderState(
                    SearchTracksState.Empty
                )
            }

            else -> {
                renderState(SearchTracksState.Content(tracks = tracks))
            }
        }

    }

    fun onClickedTrack(track: List<Track>) {
        saveTrackToHistory(track)
    }

    private fun renderState(state: SearchTracksState) {
        stateLiveData.postValue(state)
    }

    private fun saveTrackToHistory(track: List<Track>) {
        viewModelScope.launch {
            trackInteractor.saveTrackToHistory(track)
        }
    }


        suspend fun readTracksFromHistory(): List<Track> {
            return trackInteractor.readTracksFromHistory()
        }

    fun clearHistory() {
        viewModelScope.launch {
            trackInteractor.clearHistory()
            renderState(SearchTracksState.History(mutableListOf())) // Обновление состояния
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val historyTracks =  trackInteractor.readTracksFromHistory()
            renderState(SearchTracksState.History(historyTracks.toMutableList()))
        }
    }
    }


