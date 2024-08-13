package com.practicum.playlistmaker.player.ui.player

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<PlayerViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewModel.observeState().observe(this) {
            render(it)
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(CLICKED_TRACK) as? Track
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(CLICKED_TRACK) as? Track
        }

        track?.let {
            viewModel.preparePlayer(it)
            setupUI(it)
        }

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        viewModel.progressLiveData.observe(this) { progress ->
            binding.timer.text = progress.toString()
        }

        binding.back.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun setupUI(track: Track) {

        val cornerRadius = 8f
        val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        val releaseYear = track.releaseDate.substring(0, 4)


        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.timer.text = track.trackTimeMillis.formatToMinutesAndSeconds()

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

        binding.trackTime.text = track.trackTimeMillis.formatToMinutesAndSeconds()
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.countryTrack.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .centerCrop()
            .transform(
                RoundedCorners(
                    dpToPx(
                        cornerRadius,
                        applicationContext
                    )
                )
            )
            .placeholder(R.drawable.placeholder)
            .into(binding.albumPicture)
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Prepare -> prepare()
            is PlayerState.Play -> play()
            is PlayerState.Pause -> pause()
            is PlayerState.UpdatePlayingTime -> updatePlayingTime(state.time)
            else -> {}
        }
    }

    private fun prepare() {  //??????
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.timer.text = String.format("%02d:%02d", 0, 0)
    }

    private fun play() {
        binding.playButton.setImageResource(R.drawable.pause)
    }

    private fun pause() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }


    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }


    private fun updatePlayingTime(time: String) {
        binding.timer.text = time
    }


    private fun Long.formatToMinutesAndSeconds(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return "%02d:%02d".format(minutes, seconds)
    }

    private companion object {
        const val CLICKED_TRACK: String = "clicked_track"

    }


    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

}
