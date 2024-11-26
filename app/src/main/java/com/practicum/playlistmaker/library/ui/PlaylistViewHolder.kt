package com.practicum.playlistmaker.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.library.domain.Playlist

class PlaylistViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        with(binding) {
            playlistName.text = model.playlistName
            tracksCount.text = model.tracksCount.toString()
        }
        Glide.with(itemView).load(model.uri).centerCrop()
            .placeholder(R.drawable.placeholder).into(binding.playlistCover)
    }
}