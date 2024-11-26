package com.practicum.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getSavedPlaylists(): Flow<List<PlaylistEntity>>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)


}