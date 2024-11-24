package com.practicum.playlistmaker.player.domain.InteractorImpl

import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(
    private var playerRepository: PlayerRepository
) : PlayerInteractor {


    override var onPlayerCompletion: () -> Unit
        get() = playerRepository.onPlayerCompletion
        set(value) {
            playerRepository.onPlayerCompletion = value
        }


    override val playerDuration: Int
    get() = playerRepository.playerDuration
    override val playerCurrentPosition: Int
    get() = playerRepository.playerCurrentPosition

    override fun preparePlayer(
        track: Track,
        onPreparedListener: () -> Unit,
        onPlayerCompletion: () -> Unit
    ) {
        playerRepository.preparePlayer(track, onPreparedListener, onPlayerCompletion)
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