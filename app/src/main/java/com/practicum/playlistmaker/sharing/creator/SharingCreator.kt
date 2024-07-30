package com.practicum.playlistmaker.sharing.creator

import android.content.Context
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.InteractorImpl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object SharingCreator {

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }
}