package com.practicum.playlistmaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding


class MediatekaFragment : Fragment() {

    private var _binding: FragmentMediatekaBinding? = null
    private val binding get() = _binding!!

        private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val adapter = LibraryPagerAdapter(childFragmentManager, lifecycle)
            binding.viewPager.adapter = adapter

            tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favouriteTracks)
                    1 -> tab.text = getString(R.string.playlist)
                }
            }
            tabMediator.attach()


        }


        override fun onDestroy() {
            super.onDestroy()
            tabMediator.detach()
        }
    }