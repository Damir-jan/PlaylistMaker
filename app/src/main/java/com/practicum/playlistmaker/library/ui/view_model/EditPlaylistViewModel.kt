package com.practicum.playlistmaker.library.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.LocalStorageInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    val playlist: Playlist
) : NewPlaylistViewModel(localStorageInteractor, playlistsInteractor) {

    var playlistDb: Playlist? = null

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observeStatePlaylist(): LiveData<Playlist> = playlistLiveData

    fun getPlaylistById() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylistById(playlist.playlistId).collect { playlistById ->
                if (playlistById != null) {
                    playlistDb = playlistById
                    playlistLiveData.postValue(playlistDb!!)
                } else {
                    Log.d(
                        "PlaylistViewModel",
                        "Playlist not found for id: ${playlist.playlistId}"
                    )
                }
            }
        }
    }

    fun editPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.updatePlaylist(
                Playlist(
                    playlistId = playlistDb!!.playlistId,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = if(uri.isNullOrEmpty()) {playlistDb!!.uri} else {
                        uri
                    },
                    tracksIdInPlaylist = playlistDb!!.tracksIdInPlaylist,
                    tracksCount = playlistDb!!.tracksCount
                )
            )
        }
    }
}