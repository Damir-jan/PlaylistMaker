package com.practicum.playlistmaker.library.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.domain.Playlist

object PlaylistDbConvertor {
    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromListIntToString(tracksIdInPlaylist),
            tracksCount
        )
    }

    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromStringToListInt( tracksIdInPlaylist),
            tracksCount
        )
    }

    @TypeConverter
    private fun fromListIntToString(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    private fun fromStringToListInt(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }
}