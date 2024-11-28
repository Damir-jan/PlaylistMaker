package com.practicum.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.library.data.db.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

}