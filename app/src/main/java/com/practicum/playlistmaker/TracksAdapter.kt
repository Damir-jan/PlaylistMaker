package com.practicum.playlistmaker

import Track
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(private val tracks: List<Track>): RecyclerView.Adapter<TrackItemViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_search, parent, false)
        return TrackItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackItemViewHolder, position: Int) {

        holder.bind(tracks[position])

    }
    override fun getItemCount(): Int {
        return tracks.size
    }
}