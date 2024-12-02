package com.practicum.playlistmaker.library.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R.string.favouriteTracksUI
import com.practicum.playlistmaker.library.domain.db.FavoritesTrackInteractor
import com.practicum.playlistmaker.library.ui.state.FavoriteState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val context: Context,
    private val favoritesTrackInteractor: FavoritesTrackInteractor
) : ViewModel() {
    init {
        getFavoriteTracks()
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoritesTrackInteractor.getTracks().collect { favoriteTracks ->
                processResult(favoriteTracks)
            }
        }
    }

    private val _stateLiveData = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    val stateLiveData: LiveData<FavoriteState> get() = _stateLiveData

    private fun processResult(favoriteTracks: List<Track>) {
        if (favoriteTracks.isEmpty()) {
            renderState(FavoriteState.Empty(context.getString(favouriteTracksUI)))
        } else {
            renderState(FavoriteState.Content(favoriteTracks))
        }
    }

    private fun renderState(state: FavoriteState) {
        _stateLiveData.postValue(state)
    }


}