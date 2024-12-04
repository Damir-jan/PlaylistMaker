package com.practicum.playlistmaker.library.domain.db

import android.net.Uri

interface LocalStorageInteractor {

    fun saveImageToLocalStorage(uri: Uri): String
}