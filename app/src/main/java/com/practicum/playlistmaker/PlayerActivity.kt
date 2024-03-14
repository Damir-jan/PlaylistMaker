package com.practicum.playlistmaker

import Track
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val trackJson = intent.getStringExtra("clicked_track")
        val track = Gson().fromJson(trackJson, Track::class.java)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTimeMillis.formatToMinutesAndSeconds()
        binding.progressBar.text = track.trackTimeMillis.formatToMinutesAndSeconds()

        if (track.collectionName.isNullOrEmpty()) {
            binding.trackAlbum.visibility = View.GONE
            binding.album.visibility=View.GONE
        } else {
            binding.trackAlbum.text = track.collectionName
        }

        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.countryTrack.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.icons_padding_hint)))
            .placeholder(R.drawable.placeholder)
            .into(binding.albumPicture)

        binding.back.setOnClickListener { finish() }
        /*val trackJson = intent.getStringExtra("clicked_track")
        val track = Gson().fromJson(trackJson, Track::class.java)

        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTimeMillis: TextView = findViewById(R.id.track_time)
        val progressBar: TextView = findViewById(R.id.play_time)
        val artworkUrlView: ImageView = findViewById(R.id.albumPicture)
        val nameAlbum = findViewById<TextView>(R.id.trackAlbum)
        val releaseDate = findViewById<TextView>(R.id.trackYear)
        val nameGenre = findViewById<TextView>(R.id.trackGenre)
        val country = findViewById<TextView>(R.id.trackСountry)
        val album = findViewById<TextView>(R.id.album)

        val cornerRadius = 8f
        val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        val releaseYear = track.releaseDate.substring(0, 4)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeMillis.text = dateFormat.format(track.trackTimeMillis)
        progressBar.text = dateFormat.format(track.trackTimeMillis)

        if (track.collectionName.isNullOrEmpty()) {
            nameAlbum.visibility= View.GONE
            album.visibility=View.GONE
        } else {
            nameAlbum.text = track.collectionName
        }
        releaseDate.text = releaseYear
        nameGenre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .transform(RoundedCorners(dpToPx(cornerRadius, applicationContext)))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrlView)

        val imageBack = findViewById<ImageView>(R.id.back)
        imageBack.setOnClickListener {
            finish()
        }*/
    }
}

private fun Long.formatToMinutesAndSeconds(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return "%02d:%02d".format(minutes, seconds)
}

private fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()
}

