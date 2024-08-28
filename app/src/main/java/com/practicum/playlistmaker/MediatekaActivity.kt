package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding


class MediatekaActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMediatekaBinding

        private lateinit var tabMediator: TabLayoutMediator

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMediatekaBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val adapter = LibraryPagerAdapter(supportFragmentManager, lifecycle)
            binding.viewPager.adapter = adapter

            tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favouriteTracks)
                    1 -> tab.text = getString(R.string.playlist)
                }
            }
            tabMediator.attach()

            binding.back.setOnClickListener {
                finish()
            }
        }


        override fun onDestroy() {
            super.onDestroy()
            tabMediator.detach()
        }
    }