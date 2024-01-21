package com.practicum.playlistmaker

import Track
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class TrackItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textViewFirst: TextView = itemView.findViewById(R.id.textViewFirst)
    private val textViewSecond: TextView = itemView.findViewById(R.id.textViewSecond)
        private val as_TrackName: TextView = itemView.findViewById(R.id.as_TrackName)
        private val imageView: ImageView = itemView.findViewById(R.id.image_album)


    fun bind(model: Track) {
        as_TrackName.text = model.trackName
        textViewFirst.text = model.artistName
        textViewSecond.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(dpToPx(2, itemView.context)))
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(imageView)
    }
    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}