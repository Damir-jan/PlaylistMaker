package com.practicum.playlistmaker.library.ui.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.LocalStorageInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) :
    ViewModel() {

    open var playlistName = ""
    open var playlistDescription: String = ""
    open var uri: String =""
    private var tracksCount = 0

    fun saveImageToLocalStorage(uri: Uri) : String {
        return localStorageInteractor.saveImageToLocalStorage(uri)
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.createPlaylist(
                Playlist(
                    playlistId = 0,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = uri,
                    tracksIdInPlaylist = emptyList(),
                    tracksCount
                )
            )
        }
    }


}