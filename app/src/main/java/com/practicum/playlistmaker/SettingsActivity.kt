package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)

        val setButton = findViewById<ImageView>(R.id.back)
        val supportButton = findViewById<FrameLayout>(R.id.support)
        val agreementButton = findViewById<FrameLayout>(R.id.termsOfUse)
        val shareButton = findViewById<FrameLayout>(R.id.share)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            (applicationContext as App).saveThemeToSharedPreferences()
        }

        setButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        supportButton.setOnClickListener {


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
        agreementButton.setOnClickListener {

            val termsOfServiceUrl = resources.getString(R.string.termsOfServiceUrl)
            val intent = Intent(Intent.ACTION_VIEW)

            intent.data = Uri.parse(termsOfServiceUrl)
            startActivity(intent)
        }
        shareButton.setOnClickListener {
            val message = resources.getString(R.string.message)
            val courseURL = resources.getString(R.string.courseURL)
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, "$message + $courseURL")

            startActivity(intent)
        }
    }
}