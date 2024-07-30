package com.practicum.playlistmaker.player.ui.player

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayerStateInterface
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory()
        )[PlayerViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CLICKED_TRACK, Track::class.java)
        } else {
            intent.getParcelableExtra<Track>(CLICKED_TRACK)
        }

        track?.let {
            viewModel.preparePlayer(it)
            setupUI(it)
        }

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        viewModel.progressLiveData.observe(this) { progress ->
            binding.progressBar.text = progress.toString()
        }

        binding.back.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }
    /*
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
            viewModel.playbackControl()
        }*/

    private fun setupUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTimeMillis.formatToMinutesAndSeconds()
        binding.progressBar.text = track.trackTimeMillis.formatToMinutesAndSeconds()

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
    }

    private fun render(state: PlayerStateInterface) {
        when (state) {
            is PlayerStateInterface.Prepare -> prepare()
            is PlayerStateInterface.Play -> play()
            is PlayerStateInterface.Pause -> pause()
            is PlayerStateInterface.UpdatePlayingTime -> updatePlayingTime(state.time)
            else -> {}
        }
    }

    private fun prepare() {  //??????
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.progressBar.text = String.format("%02d:%02d", 0, 0)
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
        binding.progressBar.text = time
    }


    private fun Long.formatToMinutesAndSeconds(): String {
        val minutes = this / 60000
        val seconds = (this % 60000) / 1000
        return "%02d:%02d".format(minutes, seconds)
    }

    private companion object {
        const val CLICKED_TRACK: String = "clicked_track"

    }
}
