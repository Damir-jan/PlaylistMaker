package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettings2Binding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettings2Binding
    private lateinit var viewModelSettings: SettingsViewModel //1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettings2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelSettings = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]  //2


        viewModelSettings.getThemeLiveData().observe(this) { isChecked ->

            binding.themeSwitcher.isChecked = isChecked
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.termsOfUse.setOnClickListener {
            viewModelSettings.clickOpenTerms()
        }

        binding.support.setOnClickListener {
            viewModelSettings.clickOpenSupport()
        }

        binding.share.setOnClickListener {
            viewModelSettings.clickShareApp()
        }


        binding.themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            viewModelSettings.clickSwitchTheme(isChecked)
        }

//
    }
}