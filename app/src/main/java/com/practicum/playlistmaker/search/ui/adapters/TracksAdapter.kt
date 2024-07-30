package com.practicum.playlistmaker.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TracksAdapter(
    //private val tracks: MutableList<Track>,
    private val onClickListener: (clickedTrack : Track) -> Unit
) : RecyclerView.Adapter<TrackItemViewHolder>() {

    var tracks: MutableList<Track> = mutableListOf()

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
    fun setData(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
    }
   // fun interface TrackClickListener {
   //   fun onTrackClick(track: Track)
   //}
}