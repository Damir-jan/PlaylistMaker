package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerInteractor {


    var onPlayerCompletion: () -> Unit

    val playerDuration: Int
    val playerCurrentPosition: Int
    //fun getPlayerCurrentPosition(): Int
    //fun getPlayerDuration(): Int


    fun preparePlayer(track: Track)
    fun startPlayer()
    fun pausePlayer()
    fun isPlaying(): Boolean
    fun releasePlayer()
}
