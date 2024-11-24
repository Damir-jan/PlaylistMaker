package com.practicum.playlistmaker.library.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.library.domain.db.LocalStorageInteractor
import com.practicum.playlistmaker.library.domain.repository.LocalStorageRepository

class LocalStorageInteractorImpl(private val localStorageRepository: LocalStorageRepository) :
    LocalStorageInteractor {

    override fun saveImageToLocalStorage(uri: Uri) {
        localStorageRepository.saveImageToLocalStorage(uri)
    }

}