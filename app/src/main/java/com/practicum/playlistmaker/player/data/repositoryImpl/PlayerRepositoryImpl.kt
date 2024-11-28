package com.practicum.playlistmaker.player.data.repositoryImpl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {


    private var localOnPlayerCompletion: () -> Unit = {}

    override var onPlayerCompletion: () -> Unit
        get() = localOnPlayerCompletion
        set(value) {
            localOnPlayerCompletion = value
        }


    override val playerDuration: Int
        get() = mediaPlayer.duration
    override val playerCurrentPosition: Int
        get() = mediaPlayer.currentPosition


    override fun preparePlayer(
        track: Track,
        onPreparedListener: () -> Unit,
        onPlayerCompletion: () -> Unit
    ) {
        if (track.previewUrl != null) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                onPreparedListener.invoke()
            }
            mediaPlayer.setOnCompletionListener {
                onPlayerCompletion.invoke()
            }
        } else {
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()

    }

    override fun pausePlayer() {
        mediaPlayer.pause()

    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun resetPlayer() {
        mediaPlayer.reset()
    }
}