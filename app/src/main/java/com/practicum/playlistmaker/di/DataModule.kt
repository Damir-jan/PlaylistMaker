package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.NetworkClient
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.preferences.SharedPreferencesSearchClient
import com.practicum.playlistmaker.search.data.preferences.SharedPreferencesSearchClientImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val SEARCH_HISTORY_PREFERENCES = "playlist_maker_search_history_preferences"
private const val THEME_PREFERENCES = "playlist_maker_theme_preferences"

val dataModule = module {



    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            SEARCH_HISTORY_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single { Gson() }

    single<SharedPreferencesSearchClient> {
        SharedPreferencesSearchClientImpl(sharedPreferences = get(), gson = get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }


    factory <MediaPlayer> {
        MediaPlayer()
    }

    single<SharedPreferences>(named("themePreferences")) {
        androidContext().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }


}