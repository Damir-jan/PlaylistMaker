package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converter.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.FavoritesTrackEntity
import com.practicum.playlistmaker.library.domain.repository.FavoritesTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesTrackRepository {

    override suspend fun likeTrack(track: Track) {
        appDatabase.favoriteTrackDao().insertFavoriteTrack(convertToTrackEntity(track))
    }

    override suspend fun unlikeTrack(track: Track) {
        appDatabase.favoriteTrackDao().deleteFavoriteTrack(convertToTrackEntity(track))
    }

    override fun getTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { convertFromTrackEntity(it) }
    }

    private fun convertFromTrackEntity(tracks: List<FavoritesTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavoritesTrackEntity {
        return trackDbConvertor.map(track)
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        val likedIds = appDatabase.favoriteTrackDao().getFavoriteTracksId()
        return likedIds.contains(trackId)
    }
}