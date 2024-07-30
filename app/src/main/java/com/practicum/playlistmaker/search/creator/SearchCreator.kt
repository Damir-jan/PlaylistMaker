package com.practicum.playlistmaker.search.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.preferences.SharedPreferencesSearchClientImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl

const val SEARCH_HISTORY_PREFERENCES = "playlist_maker_search_history_preferences"

object SearchCreator {
    private fun getTrackRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(
            RetrofitNetworkClient(context), SharedPreferencesSearchClientImpl(
                context.getSharedPreferences(
                    SEARCH_HISTORY_PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                )
            )
        )
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }
}