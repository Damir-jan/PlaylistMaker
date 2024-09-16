package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettings2Binding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettings2Binding? = null
    private val binding get() = _binding!!
    private val viewModelSettings by viewModel<SettingsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettings2Binding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModelSettings.getThemeLiveData().observe(viewLifecycleOwner) { isChecked ->

            binding.themeSwitcher.isChecked = isChecked
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}