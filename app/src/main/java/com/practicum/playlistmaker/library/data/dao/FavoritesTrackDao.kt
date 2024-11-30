package com.practicum.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.FavoritesTrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoritesTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(track: FavoritesTrackEntity)

    @Delete(entity = FavoritesTrackEntity::class)
    suspend fun deleteFavoriteTrack(track: FavoritesTrackEntity)

    @Query("SELECT * FROM favorite_table ORDER BY time DESC")
    fun getFavoriteTracks(): Flow<List<FavoritesTrackEntity>> //change


    @Query("SELECT trackId FROM favorite_table")
    suspend fun getFavoriteTracksId(): List<Int>
}