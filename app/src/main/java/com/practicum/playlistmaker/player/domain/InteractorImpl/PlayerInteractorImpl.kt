package com.practicum.playlistmaker.player.domain.InteractorImpl

import PlayerRepository
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(
    private var playerRepository: PlayerRepository
) : PlayerInteractor {


    override var onPlayerCompletion: () -> Unit
        get() = playerRepository.onPlayerCompletion
        set(value) {
            playerRepository.onPlayerCompletion = value
        }

   /* override fun getPlayerCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun getPlayerDuration(): Int {
        return playerRepository.getDuration()
    }*/

    override val playerDuration: Int
    get() = playerRepository.playerDuration
    override val playerCurrentPosition: Int
    get() = playerRepository.playerCurrentPosition

    override fun preparePlayer(track: Track) {
        playerRepository.preparePlayer(track)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

}