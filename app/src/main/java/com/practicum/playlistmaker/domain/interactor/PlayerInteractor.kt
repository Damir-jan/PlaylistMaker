package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.models.Track

interface PlayerInteractor {

    var onPlayerStateChanged: (state: Int) -> Unit
    var onPlayerCompletion: () -> Unit
    val playerDuration: Int
    val playerCurrentPosition: Int

    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
}
