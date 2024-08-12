package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.creator.PlayerCreator
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
) : AndroidViewModel(application) {


    companion object {
        const val DELAY = 500L
        const val TRACK_FINISH = 29_900L
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

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

//

    init {
        playerInteractor.onPlayerCompletion = {
            renderState(PlayerState.Prepare)
            Log.d(MISTAKE, "Completed")
            mainThreadHandler?.removeCallbacksAndMessages(null)
            releasePlayer()
        }
    }


    private fun updateTimer() {
        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val maxTrackDuration: Long =
                        if (playerInteractor.playerDuration > TRACK_FINISH) {
                            TRACK_FINISH
                        } else {
                            (playerInteractor.playerDuration.toLong())
                        }

                            if (playerInteractor.playerCurrentPosition < maxTrackDuration) {
                                renderState(PlayerState.UpdatePlayingTime(SimpleDateFormat(
                                    "mm:ss",
                                    Locale.getDefault()
                                ).format(playerInteractor.playerCurrentPosition)))
                            } else {
                                renderState((PlayerState.Prepare))
                            }

                    mainThreadHandler?.postDelayed(
                        this,
                        DELAY,
                    )

                }
            },
            DELAY
        )
    }
    /*private fun updateTimer() {
        val handlerThread = HandlerThread("UpdateProgressBar")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = playerInteractor.getPlayerCurrentPosition()
                    val totalTime = playerInteractor.getPlayerDuration()
                    val progress = (currentPosition.toFloat() / totalTime.toFloat() * 100).toInt()
                    handler.postDelayed(this, DELAY)
                } else {
                    handlerThread.quitSafely()
                }
            }
        })

    }*/


    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }


     fun preparePlayer(track: Track) {
        renderState(PlayerState.Prepare)
        playerInteractor.preparePlayer(track)
         playerInteractor.onPlayerCompletion  = {
             renderState(PlayerState.Prepare)
             mainThreadHandler?.removeCallbacksAndMessages(null)
         }
    }


    fun updatePlayPauseIcon(isPlaying: Boolean) {
        if (isPlaying) {
        } else {
        }
    }

     fun startPlayer() {
        renderState(PlayerState.Play)
        playerInteractor.startPlayer()
        updateTimer()
        Log.d(MISTAKE, "Start")
    }

     fun pausePlayer() {
        renderState(PlayerState.Pause)
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