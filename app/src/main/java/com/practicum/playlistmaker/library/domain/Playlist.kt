package com.practicum.playlistmaker.library.domain

import java.io.Serializable

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val uri: String,
    val tracksIdInPlaylist: List<Int>,
    val tracksCount: Int
) : Serializable