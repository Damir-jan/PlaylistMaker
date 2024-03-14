package com.practicum.playlistmaker

import Track
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private companion object {
        const val CLICKED_TRACK: String = "clicked_track"
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val trackJson = intent.getStringExtra(CLICKED_TRACK)
        val track = Gson().fromJson(trackJson, Track::class.java)

        mediaPlayer = MediaPlayer()
        preparePlayer(track)


        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTimeMillis.formatToMinutesAndSeconds()
        binding.progressBar.text = track.trackTimeMillis.formatToMinutesAndSeconds()
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.playButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )

        if (track.collectionName.isNullOrEmpty()) {
            binding.trackAlbum.visibility = View.GONE
            binding.album.visibility = View.GONE
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

        binding.playButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        updateProgressBar()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateProgressBar() {
        val handlerThread = HandlerThread("UpdateProgressBar")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val totalTime = mediaPlayer.duration
                    val progress = (currentPosition.toFloat() / totalTime.toFloat() * 100).toInt()
                    runOnUiThread {
                        binding.progressBar.text =
                            currentPosition.toLong().formatToMinutesAndSeconds()
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        })

    }
}


    private fun Long.formatToMinutesAndSeconds(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return "%02d:%02d".format(minutes, seconds)
    }




