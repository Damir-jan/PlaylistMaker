package com.practicum.playlistmaker.search.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.creator.SearchCreator
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchTracksState

class SearchTracksViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val state = MutableLiveData<SearchTracksState>()

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTracksViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val trackInteractor =
        SearchCreator.provideTrackInteractor(getApplication<Application>())

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchTracksState>()
    fun observeState(): LiveData<SearchTracksState> = stateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchTrack(newSearchText: String) {
        if (newSearchText.isBlank()
        ) {
            return
        }

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        renderState(SearchTracksState.Loading)

        trackInteractor.searchTracks(newSearchText, object : TrackInteractor.TrackConsumer {
            override fun consume(
                foundTracks: List<Track>?,
                errorMessage: String?
            ) {
                val tracks = mutableListOf<Track>()

                if (foundTracks != null) {
                    tracks.clear()
                    tracks.addAll(foundTracks)
                }
                when {
                    errorMessage != null -> {
                        Log.d("TEST", "errorMessage")
                        renderState(
                            SearchTracksState.Error(
                                errorMessage = getApplication<Application>().getString(R.string.noInternetError)
                            )
                        )
                    }

                    foundTracks?.isEmpty() == true -> {
                        Log.d("TEST", "isEmpty")
                        renderState(
                            SearchTracksState.Empty(
                                message = getApplication<Application>().getString(
                                    R.string.nothingToFind
                                )
                            )
                        )
                    }

                    else -> {
                        renderState(SearchTracksState.Content(tracks = tracks))
                    }
                }
            }
        }
        )
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

    fun readTracksFromHistory(): Array<Track> {
        return trackInteractor.readTracksFromHistory()
    }

    fun clearHistory() {
        trackInteractor.clearHistory()
    }
}