package com.practicum.playlistmaker.library.domain.repository

import android.net.Uri

interface LocalStorageRepository {

    fun saveImageToLocalStorage(uri: Uri) : String
}

