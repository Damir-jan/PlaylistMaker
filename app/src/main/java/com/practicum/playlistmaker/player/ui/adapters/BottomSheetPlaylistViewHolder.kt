package com.practicum.playlistmaker.player.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.BottomSheetViewBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.utils.TraksCount

class BottomSheetPlaylistViewHolder (private val binding: BottomSheetViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, clickListener: ((Int, List<Int>, Playlist) -> Unit)?) {
        with(binding) {
            playlistNameTv.text = playlist.playlistName
            tracksCount.text = binding.root.context.getString(
                R.string.track_count_format,
                playlist.tracksCount,
                TraksCount.getTracksCountText(playlist.tracksCount)
            )
        }
        Glide.with(itemView).load(playlist.uri).centerCrop()
            .placeholder(R.drawable.placeholder).into(binding.playlistCover)


        binding.root.setOnClickListener {
            clickListener?.invoke(adapterPosition, playlist.tracksIdInPlaylist, playlist)
        }
    }
}