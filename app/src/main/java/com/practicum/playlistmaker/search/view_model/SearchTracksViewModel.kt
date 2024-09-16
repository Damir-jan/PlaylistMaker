package com.practicum.playlistmaker.search.view_model

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
            tracks.clear()   //нет в примере
            tracks.addAll(foundTracks)
        }
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
        trackInteractor.saveTrackToHistory(track)
    }

    fun readTracksFromHistory(): List<Track> {
        return trackInteractor.readTracksFromHistory()
    }

    fun clearHistory() {
        trackInteractor.clearHistory()
    }
}