package com.practicum.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.library.ui.state.TracksInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.utils.TrackTimeConverter
import com.practicum.playlistmaker.utils.TraksCount
import com.practicum.playlistmaker.utils.TraksCount.getTracksCountText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistSomeViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor) : ViewModel() {

    private var playlist : Playlist? = null
    private var tracksList = arrayListOf<Track>() as List<Track>

    companion object {
        private const val tenMinutes : Long = 600000
    }

    private val playlistLiveData = MutableLiveData<Playlist>()

    private val tracksInPlaylistLiveData = MutableLiveData<List<Track>>()
    private val totalTracksDurationLiveData = MutableLiveData<String>()
    private val renderTracksLiveData = MutableLiveData<TracksInPlaylistState>()

    fun observeStatePlaylist(): LiveData<Playlist> = playlistLiveData
    fun observeStateTracksInPlaylist(): LiveData<List<Track>> = tracksInPlaylistLiveData
    fun observeStateTotalTracksDuration(): LiveData<String> = totalTracksDurationLiveData
    fun observeStateRenderTracksLiveData(): LiveData<TracksInPlaylistState> = renderTracksLiveData



    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylistById(playlistId).collect { playlistById ->
                if(playlistById !=null ) {
                    playlist = playlistById
                    playlistLiveData.postValue(playlist!!)
                    getTracksInPlaylistById(playlist!!.tracksIdInPlaylist)
                } else {
                }
            }
        }
    }

    fun getTracksInPlaylistById(tracksIdInPlaylist: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getTracksInPlaylistsById(tracksIdInPlaylist)
                .collect { tracksInPlaylist ->
                    tracksList = tracksInPlaylist
                    tracksInPlaylistLiveData.postValue(tracksList)
                    getTotalTracksDuration(tracksList)
                    renderTracksInPlaylist(tracksList)
                }
        }
    }

    private fun getTotalTracksDuration(tracksInPlaylist: List<Track>) {
        var totalTracksDuration: Long = 0
        tracksInPlaylist?.forEach { track ->
            totalTracksDuration += track.trackTimeMillis
        }
        val timeInMin: String
        if (totalTracksDuration < tenMinutes) {
            timeInMin = TrackTimeConverter.milsToToLessThan10Min(totalTracksDuration)
        } else {
            timeInMin = TrackTimeConverter.milsToMinSec(totalTracksDuration)
        }
        totalTracksDurationLiveData.postValue(timeInMin + TraksCount.getMinutesWord(timeInMin.toInt()))
    }

    private fun renderTracksInPlaylist(tracksInPlaylist : List<Track>) {
        if (tracksInPlaylist.isEmpty()) {
            renderTracksInPlaylistState(TracksInPlaylistState.Empty)
        } else {
            renderTracksInPlaylistState(TracksInPlaylistState.Content(tracksInPlaylist))
        }
    }

    private fun renderTracksInPlaylistState(state: TracksInPlaylistState) {
        renderTracksLiveData.postValue(state)
    }

    fun deleteTrackInPlaylist(trackId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.deleteTrackInPlaylist(playlist!!, trackId!!)
        }
    }
    fun sharePlaylist() {
        var trackInfo: String = ""
        var i = 1
        tracksList.forEach { track ->
            trackInfo += "$i. " +
                    "${track.artistName} - " +
                    "${track.trackName} " +
                    "(${TrackTimeConverter.milsToMinSec(track.trackTimeMillis)})\n"
            i++
        }

        if (playlist != null) {
            val message: String =
                "Плейлист \"${playlist!!.playlistName}\"\n" +
                        "${playlist!!.playlistDescription ?: ""}\n" +
                        "${playlist!!.tracksCount} ${getTracksCountText(playlist!!.tracksCount)}:\\n" +
                        "${trackInfo}"
            sharingInteractor.sharePlaylist(message)
        }
    }

    fun deletePlaylist() {
        if (playlist != null) {
            viewModelScope.launch(Dispatchers.IO) {
                playlistsInteractor.deletePlaylist(playlist!!)
            }
        }
    }
}