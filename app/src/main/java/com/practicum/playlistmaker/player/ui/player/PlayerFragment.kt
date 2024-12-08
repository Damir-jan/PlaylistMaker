package com.practicum.playlistmaker.player.ui.player

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.adapters.BottomSheetPlaylistAdapter
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.TrackInPlaylistState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.TrackTimeConverter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private var adapter: BottomSheetPlaylistAdapter? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private companion object {
        const val CLICKED_TRACK: String = "clicked_track"
    }

    private val viewModel by viewModel<PlayerViewModel>()

    private val args: PlayerFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = args.track

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        track?.let {
            viewModel.preparePlayer(track)
            setupUI(track)
        }

        viewModel.progressLiveData.observe(viewLifecycleOwner) { progress ->
            binding.timer.text = progress.toString()
        }


        viewModel.observeFavoriteState().observe(viewLifecycleOwner) {
            renderLikeButton(it)
        }
        viewModel.observeTrackInPlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackInPlaylistState.TrackAddToPlaylist -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_add_now) + " ${state.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.markTrackAddedHandled()
                }

                is TrackInPlaylistState.TrackIsAlreadyInPlaylist -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.track_already_add_to_playlist) + " ${state.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.markTrackAddedHandled()
                }

                else -> {}
            }
        }


        binding.back.setOnClickListener {
            findNavController().navigateUp() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
        binding.likeButton.setOnClickListener {
            if (track != null) {
                viewModel.onFavoriteClicked(track)
            }
        }
        binding.addButton.setOnClickListener {
            viewModel.getSavedPlaylists()
            showBottomSheet()
        }

        adapter = BottomSheetPlaylistAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })

        viewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            adapter?.playlists?.clear()
            adapter?.playlists?.addAll(playlists)
            adapter?.notifyDataSetChanged()
        }

        adapter?.itemClickListener = { position, tracksId, playlist ->
            viewModel.addTracksIdInPlaylist(playlist, tracksId, track)
        }


        binding.newPlaylist.setOnClickListener {
            val action = PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupUI(track: Track) {

        val cornerRadius = 8f



        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.timer.text = viewModel.setStartTime()

        binding.playButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        if (track.collectionName.isNullOrEmpty()) {
            binding.trackAlbum.visibility = View.GONE
            binding.album.visibility = View.GONE
        } else {
            binding.trackAlbum.text = track.collectionName
        }

        binding.trackTime.text = TrackTimeConverter.milsToMinSec(track.trackTimeMillis)
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.countryTrack.text = track.country
        Glide.with(requireContext())
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .centerCrop()
            .transform(
                RoundedCorners(
                    dpToPx(
                        cornerRadius,
                        requireContext()
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

    private fun prepare() {
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
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetPlayer()
    }
    private fun updatePlayingTime(time: String) {
        binding.timer.text = time
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

    private fun renderLikeButton(isFavorite: Boolean) {
        val imageResource =
            if (isFavorite) {
                R.drawable.like_button
            } else {
                R.drawable.dislike_button
            }
        binding.likeButton.setImageResource(imageResource)
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}