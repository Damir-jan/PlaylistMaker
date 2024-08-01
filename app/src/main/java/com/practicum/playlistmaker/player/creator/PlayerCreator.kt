package com.practicum.playlistmaker.player.creator

import PlayerRepository
import com.practicum.playlistmaker.player.data.repositoryImpl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.InteractorImpl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor

object PlayerCreator {
    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}