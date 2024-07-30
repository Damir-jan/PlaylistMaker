package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchTracksState {

    object Loading : SearchTracksState

    data class Content(
        val tracks: MutableList<Track>
    ) : SearchTracksState

    data class Error(
        val errorMessage: String
    ) : SearchTracksState

    data class Empty(
        val message: String
    ) : SearchTracksState


}
