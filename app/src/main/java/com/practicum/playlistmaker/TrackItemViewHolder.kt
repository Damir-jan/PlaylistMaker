package com.practicum.playlistmaker

import Track
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textViewFirst: TextView = itemView.findViewById(R.id.textViewFirst)
    private val textViewSecond: TextView = itemView.findViewById(R.id.textViewSecond)
    private val imageViewVector: ImageView = itemView.findViewById(R.id.imageViewVector)
        private val as_TrackName: TextView = itemView.findViewById(R.id.as_TrackName)
        private val imageView: ImageView = itemView.findViewById(R.id.image_album)


    fun bind(model: Track) {
        as_TrackName.text = model.trackName
        textViewFirst.text = model.artistName
        textViewSecond.text = model.trackTime

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(2))
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(imageView)
    }
}