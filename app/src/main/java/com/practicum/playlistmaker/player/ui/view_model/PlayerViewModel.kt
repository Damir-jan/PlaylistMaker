package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesTrackInteractor
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor : PlayerInteractor,
    private val favoritesTrackInteractor: FavoritesTrackInteractor
) : ViewModel() {





    private var timerJob: Job? = null


    private val _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> get() = _progressLiveData

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteLiveData




    private fun updateTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                stateLiveData.postValue(PlayerState.UpdatePlayingTime(getCurrentPlayerPosition()))
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
        stateLiveData.postValue(state)
    }

    fun preparePlayer(track: Track) {
        favoriteLiveData.postValue(track.isFavorite)
        playerInteractor.preparePlayer(
            track,
            onPreparedListener = {
                renderState(PlayerState.Prepare(track))
            },
            onPlayerCompletion = {
                renderState(PlayerState.Prepare(track))
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

     fun releasePlayer() {
        playerInteractor.releasePlayer()

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
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesTrackInteractor.unlikeTrack(track)
            } else {
                favoritesTrackInteractor.likeTrack(track)
            }
            track.isFavorite = !track.isFavorite
            favoriteLiveData.postValue(track.isFavorite)
        }
    }

    fun initLikeButton(isLiked: Boolean) {
        favoriteLiveData.value = isLiked
    }

}
