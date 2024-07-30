package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.creator.PlayerCreator
import com.practicum.playlistmaker.player.ui.models.PlayerStateInterface
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var mediaPlayer = MediaPlayer()

    companion object {
        const val DELAY = 500L
        const val MISTAKE = "Mistake"

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val playerInteractor =
        PlayerCreator.providePlayerInteractor()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int> get() = _progressLiveData

    private val stateLiveData = MutableLiveData<PlayerStateInterface>()
    fun observeState(): LiveData<PlayerStateInterface> = stateLiveData

//

    init {
        playerInteractor.onPlayerCompletion = {
            renderState(PlayerStateInterface.Prepare)
            Log.d(MISTAKE, "Completed")
            mainThreadHandler?.removeCallbacksAndMessages(null)  //gpt
            releasePlayer() //gpt
        }
    }

    private fun updateProgressBar() {
        val handlerThread = HandlerThread("UpdateProgressBar")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = playerInteractor.getPlayerCurrentPosition()
                    val totalTime = playerInteractor.getPlayerDuration()
                    val progress = (currentPosition.toFloat() / totalTime.toFloat() * 100).toInt()
                    _progressLiveData.postValue(progress)
                    handler.postDelayed(this, DELAY)
                } else {
                    handlerThread.quitSafely()
                }
            }
        })

    }


    private fun renderState(state: PlayerStateInterface) {
        stateLiveData.postValue(state)
    }


    fun preparePlayer(track: Track) {
        renderState(PlayerStateInterface.Prepare)
        playerInteractor.preparePlayer(track)
        Log.d(MISTAKE, "Prepare")
    }

    fun startPlayer() {
        renderState(PlayerStateInterface.Play)
        playerInteractor.startPlayer()
        updateProgressBar()
        Log.d(MISTAKE, "Start")
    }

    fun pausePlayer() {
        renderState(PlayerStateInterface.Pause)
        playerInteractor.pausePlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        Log.d(MISTAKE, "Pause")
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        Log.d(MISTAKE, "Destroy")
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