package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettings2Binding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettings2Binding
    private val viewModelSettings by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettings2Binding.inflate(layoutInflater)
        setContentView(binding.root)



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