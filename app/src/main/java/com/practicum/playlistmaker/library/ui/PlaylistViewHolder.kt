package com.practicum.playlistmaker.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.utils.TraksCount.getTracksCountText

class PlaylistViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        with(binding) {
            playlistName.text = playlist.playlistName
            tracksCount.text = getTracksCountText(playlist.tracksCount)
        }
        Glide.with(itemView).load(playlist.uri).centerCrop()
            .placeholder(R.drawable.placeholder).into(binding.playlistCover)
    }
}