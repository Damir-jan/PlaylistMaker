package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        val set_button = findViewById<ImageView>(R.id.Back)
        val support_button = findViewById<FrameLayout>(R.id.Support)
        val agreement_button = findViewById<FrameLayout>(R.id.TermsOfUse)
        val share_button = findViewById<FrameLayout>(R.id.Share)

        set_button.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        support_button.setOnClickListener {


            val email = resources.getString(R.string.supportEmail)
            val subject = resources.getString(R.string.supportSubject)
            val mail = resources.getString(R.string.supportMail)

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, mail)
            startActivity(intent)

        }
        agreement_button.setOnClickListener {

            val termsOfServiceUrl = resources.getString(R.string.termsOfServiceUrl)
            val intent = Intent(Intent.ACTION_VIEW)

            intent.data = Uri.parse(termsOfServiceUrl)
            startActivity(intent)
        }
        share_button.setOnClickListener {
            val message = resources.getString(R.string.message)
            val courseURL = resources.getString(R.string.courseURL)
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, "$message + $courseURL")

            startActivity(intent)
        }
    }
}