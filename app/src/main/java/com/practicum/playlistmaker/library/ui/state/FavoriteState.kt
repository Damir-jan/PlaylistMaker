package com.practicum.playlistmaker.library.ui.state

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteState {
    data class Content(
        val favoriteTracks: List<Track>
    ) : FavoriteState

    data class Empty(
        val message: String
    ) : FavoriteState

}