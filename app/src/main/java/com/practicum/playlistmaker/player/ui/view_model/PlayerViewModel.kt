package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.FavoritesTrackInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.TrackInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor : PlayerInteractor,
    private val favoritesTrackInteractor: FavoritesTrackInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {





    private var timerJob: Job? = null


    private val _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> get() = _progressLiveData

    private val playerLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = playerLiveData

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    private val trackInPlaylistLiveData = MutableLiveData<TrackInPlaylistState>()
    fun observeTrackInPlaylistState(): LiveData<TrackInPlaylistState> = trackInPlaylistLiveData

    private fun updateTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                playerLiveData.postValue(PlayerState.UpdatePlayingTime(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.playerCurrentPosition) ?: "00:00"
    }



    private fun renderState(state: PlayerState) {
        playerLiveData.postValue(state)
    }

    fun preparePlayer(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = favoritesTrackInteractor.isTrackFavorite(track.trackId)
            favoriteLiveData.postValue(isFavorite)
        }
        playerInteractor.preparePlayer(
            track,
            onPreparedListener = {
                renderState(PlayerState.Prepare)
            },
            onPlayerCompletion = {
                renderState(PlayerState.Prepare)
                timerJob?.cancel()
            }
        )
    }




     fun startPlayer() {
        renderState(PlayerState.Play)
        playerInteractor.startPlayer()
        updateTimer()
    }

     fun pausePlayer() {
        renderState(PlayerState.Pause)
        playerInteractor.pausePlayer()
         timerJob?.cancel()


    }

    fun resetPlayer() {
        playerInteractor.resetPlayer()

    }

     fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }

     fun playbackControl() {
        if (isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun setStartTime(): String {
        return String.format("%02d:%02d", 0, 0)
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch(Dispatchers.IO){
            if (favoritesTrackInteractor.isTrackFavorite(track.trackId)) {
                favoritesTrackInteractor.unlikeTrack(track)
                favoriteLiveData.postValue(false)
            } else {
                favoritesTrackInteractor.likeTrack(track)
                favoriteLiveData.postValue(true)
            }
        }
    }
    fun getSavedPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getSavedPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }

    fun addTracksIdInPlaylist(playlist: Playlist, tracksId: List<Int>, track: Track) {
        if (track.trackId in tracksId) {
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackIsAlreadyInPlaylist(playlist.playlistName))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                playlistsInteractor.addTracksIdInPlaylist(playlist, tracksId, track)
            }
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackAddToPlaylist(playlist.playlistName))
        }
    }

}
