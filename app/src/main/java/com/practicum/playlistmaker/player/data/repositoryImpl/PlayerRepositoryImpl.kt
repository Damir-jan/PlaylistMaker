package com.practicum.playlistmaker.player.data.repositoryImpl

import PlayerRepository
import android.media.MediaPlayer
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerRepositoryImpl(
) : PlayerRepository {

    private val mediaPlayer = MediaPlayer()

    private var localOnPlayerCompletion: () -> Unit = {}

    override var onPlayerCompletion: () -> Unit
        get() = localOnPlayerCompletion
        set(value) {
            localOnPlayerCompletion = value
        }

    /*override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }*/

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

    }
    override fun pausePlayer() {
        mediaPlayer.pause()

    }
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
    override fun releasePlayer() {
        mediaPlayer.release()
    }
}