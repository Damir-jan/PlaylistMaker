package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val SearchButton = findViewById<Button>(R.id.Search)
        val MediatekaButton = findViewById<Button>(R.id.Mediateka)
        val SettingsButton = findViewById<Button>(R.id.Settings)


        SettingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
        MediatekaButton.setOnClickListener {
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
        }
        SearchButton.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }
// Добавил коммент чтобы получилось создать PR между двумя своими ветками

    }
}