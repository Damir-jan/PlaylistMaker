package com.practicum.playlistmaker.player.domain.repository


import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {

    var onPlayerCompletion: () -> Unit
    val playerDuration: Int
    val playerCurrentPosition: Int

    fun preparePlayer(track: Track, onPreparedListener: () -> Unit, onPlayerCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun releasePlayer()
}
