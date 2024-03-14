package com.practicum.playlistmaker

import Track
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private val tracks: MutableList<Track>,
    private val onClickListener: (clickedTrack : Track) -> Unit
) : RecyclerView.Adapter<TrackItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_search, parent, false)
        return TrackItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackItemViewHolder, position: Int) {

        val selectedTrack = tracks[position]
        holder.bind(selectedTrack)
        holder.itemView.setOnClickListener {
            onClickListener(selectedTrack)

        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}