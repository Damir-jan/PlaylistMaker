package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.dao.FavoritesTrackDao
import com.practicum.playlistmaker.library.data.dao.PlaylistDao
import com.practicum.playlistmaker.library.data.dao.TrackInPlaylistDao

@Database(
    version = 4,
    entities = [FavoritesTrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavoriteTrackDao(): FavoritesTrackDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getTrackInPlaylistDao(): TrackInPlaylistDao

}