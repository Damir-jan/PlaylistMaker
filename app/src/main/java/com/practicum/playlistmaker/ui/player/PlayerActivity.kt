package com.practicum.playlistmaker.ui.player

import PlayerRepository
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
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.repositoryImpl.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repositoryImpl.PlayerRepositoryImpl.Companion.STATE_COMPLETED
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.InteractorImpl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private companion object {
        const val CLICKED_TRACK: String = "clicked_track"
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

    }
    private var mainThreadHandler: Handler? = null

    private var mediaPlayer = MediaPlayer()

    private val playerRepository: PlayerRepository = PlayerRepositoryImpl(mediaPlayer)
    private val playerInteractor: PlayerInteractor = PlayerInteractorImpl(playerRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val trackJson = intent.getStringExtra(CLICKED_TRACK)
        val track = Gson().fromJson(trackJson, Track::class.java)

        mediaPlayer = MediaPlayer()
        playerInteractor.preparePlayer(track)

        playerInteractor.onPlayerStateChanged = { state ->
            when (state) {
                STATE_PLAYING -> {
                    updateProgressBar()
                    binding.playButton.setImageResource(R.drawable.pause)
                }

                STATE_PREPARED, STATE_PAUSED, STATE_COMPLETED -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
            }
        }

        playerInteractor.onPlayerCompletion = {
            binding.playButton.setImageResource(R.drawable.play_button)
            binding.progressBar.text = String.format("%02d:%02d", 0, 0)
        }

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
            playerInteractor.playbackControl()
        }
    }


    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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
                            currentPosition.toLong().toString()
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
