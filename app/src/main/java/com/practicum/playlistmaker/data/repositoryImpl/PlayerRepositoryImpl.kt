package com.practicum.playlistmaker.data.repositoryImpl

import PlayerRepository
import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.models.Track

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer

) : PlayerRepository {

    private var playerState = STATE_DEFAULT

    private var localOnPlayerStateChanged: (state: Int) -> Unit = {}
    private var localOnPlayerCompletion: () -> Unit = {}

    init {
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPlayerStateChanged(playerState)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_COMPLETED
            onPlayerStateChanged(playerState)
        }
    }

    override var onPlayerStateChanged: (state: Int) -> Unit
        get() = localOnPlayerStateChanged
        set(value) {
            localOnPlayerStateChanged = value
        }

    override var onPlayerCompletion: () -> Unit
        get() = localOnPlayerCompletion
        set(value) {
            localOnPlayerCompletion = value
        }

    override val playerDuration: Int
        get() = mediaPlayer.duration
    override val playerCurrentPosition: Int
        get() = mediaPlayer.currentPosition


    override fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        onPlayerStateChanged(playerState)

    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        onPlayerStateChanged(playerState)

    }

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4
        const val MISTAKE = "Mistake"
    }
}