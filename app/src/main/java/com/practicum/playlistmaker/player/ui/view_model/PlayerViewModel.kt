package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor : PlayerInteractor
) : ViewModel() {





    private var timerJob: Job? = null


    private val _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> get() = _progressLiveData

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData




    private fun updateTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                stateLiveData.postValue(PlayerState.UpdatePlayingTime(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.playerCurrentPosition) ?: "00:00"
    }



    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }


     fun preparePlayer(track: Track) {
        renderState(PlayerState.Prepare)
        playerInteractor.preparePlayer(track)
         playerInteractor.onPlayerCompletion  = {
             renderState(PlayerState.Prepare)
             timerJob?.cancel()
         }
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
}