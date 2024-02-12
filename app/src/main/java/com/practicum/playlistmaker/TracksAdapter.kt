package com.practicum.playlistmaker

import Track
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(private val tracks: List<Track>,
                    private val searchHistory:SearchHistory
                    ): RecyclerView.Adapter<TrackItemViewHolder> () {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_search, parent, false)
        return TrackItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackItemViewHolder, position: Int) {

        val selectedTrack = tracks[position]
        holder.bind(selectedTrack)
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Нажали на трек!", Toast.LENGTH_SHORT).show()
            searchHistory.saveTrack(mutableListOf(selectedTrack))

        }
        }
    override fun getItemCount(): Int {
        return tracks.size
    }
}