package com.practicum.playlistmaker.library.domain

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val uri: String?,
    val tracksIdInPlaylist: List<Int>,
    val tracksCount: Int
)