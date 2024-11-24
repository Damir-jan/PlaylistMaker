package com.practicum.playlistmaker.player.ui.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.BottomSheetViewBinding
import com.practicum.playlistmaker.library.domain.Playlist

class BottomSheetPlaylistViewHolder (private val binding: BottomSheetViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        with(binding) {
            playlistNameTv.text = model.playlistName
            tracksCount.text = model.tracksCount.toString()
        }
        Glide.with(itemView).load(model.uri).centerCrop()
            .placeholder(R.drawable.placeholder).into(binding.playlistCover)

        Log.d("BottomSheetPlaylistViewHolder", "Loading image from: ${model.uri}")
    }
}