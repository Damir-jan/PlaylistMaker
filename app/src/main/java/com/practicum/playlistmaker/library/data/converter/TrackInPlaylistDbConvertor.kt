package com.practicum.playlistmaker.library.data.converter

import com.practicum.playlistmaker.library.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track

object TrackInPlaylistDbConvertor {
    fun Track.toTrackInPlaylistEntity(): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    fun TrackInPlaylistEntity.toTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            trackTime,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl.toString()
        )
    }
}