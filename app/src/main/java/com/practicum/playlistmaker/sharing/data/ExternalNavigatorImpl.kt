package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {


    override fun shareLink() {
        context.startActivity(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(R.string.courseURL)
                )
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, null
        )
    }

    override fun openLink() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.terms_of_use))
            ).setFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(Companion.emailData.mailTo)
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.write_support))
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(context.getString(R.string.supportEmail))
                )
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.supportSubject))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private companion object {
        val emailData = EmailData()
    }
}