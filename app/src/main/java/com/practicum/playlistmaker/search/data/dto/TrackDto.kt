package com.practicum.playlistmaker.search.data.dto

import java.io.Serializable

data class TrackDto(
        val trackId: Int,
        val trackName: String,
        val artistName: String,
        val trackTimeMillis: Long,
        val artworkUrl100: String,
        val collectionName: String,
        val releaseDate: String,
        val primaryGenreName: String,
        val country: String,
        val previewUrl: String,
)   : Serializable