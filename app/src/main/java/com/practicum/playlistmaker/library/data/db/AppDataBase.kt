package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.dao.FavoritesTrackDao
import com.practicum.playlistmaker.library.data.dao.PlaylistDao

 @Database(version = 3, entities = [FavoritesTrackEntity::class, PlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavoriteTrackDao(): FavoritesTrackDao
    abstract fun getPlaylistDao(): PlaylistDao
}