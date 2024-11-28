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
        appDatabase.getFavoriteTrackDao().insertFavoriteTrack(convertToTrackEntity(track))
    }

    override suspend fun unlikeTrack(track: Track) {
        appDatabase.getFavoriteTrackDao().deleteFavoriteTrack(convertToTrackEntity(track))
    }

    override fun getTracks(): Flow<List<Track>> {
        return appDatabase.getFavoriteTrackDao().getFavoriteTracks()
            .map { convertFromTrackEntity(it) }
    }

    private fun convertFromTrackEntity(tracks: List<FavoritesTrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavoritesTrackEntity {
        return trackDbConvertor.map(track)
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        val likedIds = appDatabase.getFavoriteTrackDao().getFavoriteTracksId()
        return likedIds.contains(trackId)
    }
}