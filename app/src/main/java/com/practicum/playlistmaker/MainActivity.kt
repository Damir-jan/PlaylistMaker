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
        val button = findViewById<Button>(R.id.Search)
        val button2 = findViewById<Button>(R.id.Mediateka)
        val button3 = findViewById<Button>(R.id.Settings)


        button3.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
        button2.setOnClickListener {
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
        }
        button.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }
// Добавил коммент чтобы получилось создать PR между двумя своими ветками

    }
}