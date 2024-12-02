package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistSomeBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.ui.fragment.FavoriteTracksFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.library.ui.state.TracksInPlaylistState
import com.practicum.playlistmaker.library.ui.view_model.PlaylistSomeViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TracksAdapter
import com.practicum.playlistmaker.utils.TraksCount
import debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistSomeFragment : Fragment() {

    private var _binding: FragmentPlaylistSomeBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistSomeFragmentArgs by navArgs()

    private val playlistSomeViewModel: PlaylistSomeViewModel by viewModel<PlaylistSomeViewModel>()

    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var openPlayerDebounce: (Track) -> Unit

    private val trackInPlaylistClickListener: (Track) -> Unit = { clickedTrack ->
        openPlayerDebounce(clickedTrack)
    }

    private var adapter: TracksAdapter? = null

    private var playlist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistSomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistSomeViewModel.getPlaylistById(args.playlist.playlistId ?: 0)
        initObservers()

        initButtomSheet()

        adapter = TracksAdapter(trackInPlaylistClickListener).apply {
            onLongClickListener =
                { clickedTrack ->
                    showConfirmDialogDeleteTrack(clickedTrack)
                }
        }
        binding.tracksInPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksInPlaylist.adapter = adapter

        binding.back.setOnClickListener() {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })

        openPlayerDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { clickedTrack ->
            val action =
                PlaylistSomeFragmentDirections.actionPlaylistSomeFragmentToPlayerFragment(
                    clickedTrack
                )
            findNavController().navigate(action)
        }
        binding.share.setOnClickListener {
            sharePlaylist()
        }

        binding.menuShare.setOnClickListener {
            sharePlaylist()
        }

        with(binding) {
            menu.setOnClickListener {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                menuBottomSheet.isVisible = true

                menuBottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onStateChanged(bottomSheet: View, newState: Int) {

                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                overlay.visibility = View.GONE
                            }

                            else -> {
                                overlay.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }

        binding.menuDeletePlaylist.setOnClickListener {
            if (playlist != null) {
                showConfirmDialogDeletePlaylist(playlist!!)
            }
        }

        binding.menuEditInfo.setOnClickListener {
            if (playlist != null) {
                val action =
                    PlaylistSomeFragmentDirections.actionPlaylistSomeFragmentToEditPlaylistFragment(
                        playlist!!
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tracksInPlaylist.adapter = null
        _binding = null
        adapter = null
    }

    private fun setPlaylistData(playlist: Playlist) {
        with(binding) {
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.playlistDescription

            setImageResource(playlistImage, playlist)
            setTracksCount(tracksCount, playlist)
        }
    }

    private fun setMenuData(playlist: Playlist) {
        with(binding) {
            infoPlaylistBriefInfo.playlistNameTv.text = playlist.playlistName
            setImageResource(infoPlaylistBriefInfo.playlistCover, playlist)
            setTracksCount(infoPlaylistBriefInfo.tracksCount, playlist)
        }
    }

    private fun setImageResource(imageView: ImageView, playlist: Playlist) {
        with(binding) {
            if (playlist.uri.isNullOrEmpty()) {

                imageView.setImageResource(R.drawable.placeholder)
            } else {
                imageView.setImageURI(playlist.uri?.toUri())
            }
        }
    }

    private fun setTracksCount(textView: TextView, playlist: Playlist) {
        textView.text = binding.root.context.getString(
            R.string.track_count_format,
            playlist.tracksCount,
            TraksCount.getTracksCountText(playlist.tracksCount)
        )
    }


    fun initButtomSheet() {
        with(binding) {
            playlistBottomSheetBehavior = BottomSheetBehavior.from(playlistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED

                menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheet).apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                    menuBottomSheet.isVisible = false
                }
            }

            menu.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    val screenHeight = resources.displayMetrics.heightPixels

                    val menuPosition = IntArray(2)
                    menu.getLocationOnScreen(menuPosition)

                    val marginInPx = resources.getDimensionPixelSize(R.dimen.header_indent_24)
                    val menuBottomY =
                        menuPosition[1] + marginInPx

                    val bottomSheetPeekHeight = screenHeight - menuBottomY

                    playlistBottomSheetBehavior.peekHeight = bottomSheetPeekHeight

                    menu.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

            playlistName.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    val screenHeight = resources.displayMetrics.heightPixels
                    val playlistNamePosition = IntArray(2)
                    playlistName.getLocationOnScreen(playlistNamePosition)
                    val playlistNameBottomY = playlistNamePosition[1]
                    val bottomSheetPeekHeight = screenHeight - playlistNameBottomY

                    menuBottomSheetBehavior.peekHeight = bottomSheetPeekHeight

                    playlistName.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    private fun initObservers() {
        playlistSomeViewModel.observeStatePlaylist().observe(viewLifecycleOwner) { playlist ->
            this.playlist = playlist
            setPlaylistData(playlist)
            setMenuData(playlist)
        }

        playlistSomeViewModel.observeStatePlaylist().observe(viewLifecycleOwner) {
            playlistSomeViewModel.observeStateRenderTracksLiveData().observe(viewLifecycleOwner) {
                render(it)
            }
        }

        playlistSomeViewModel.observeStateTotalTracksDuration()
            .observe(viewLifecycleOwner) { duration ->
                Log.d("CheckDuration", "observeStateTotalTracksDuration")
                binding.playlistDuration.text = duration
            }
    }

    private fun render(state: TracksInPlaylistState) {
        when (state) {
            is TracksInPlaylistState.Content -> showContent(state.tracksInPlaylist)
            is TracksInPlaylistState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            tracksInPlaylist.isVisible = false
            placeholderMessage.isVisible = true
            placeholderMessage.setText(R.string.playlist_is_empty)
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            tracksInPlaylist.isVisible = true
            placeholderMessage.isVisible = false
        }
        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showConfirmDialogDeleteTrack(track: Track) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_track_from_playlist)
            .setNeutralButton(R.string.cancel) { dialog, which -> }
            .setPositiveButton(R.string.delete) { dialog, which ->
                playlistSomeViewModel.deleteTrackInPlaylist(track.trackId)
            }.show()

    }

    private fun showConfirmDialogDeletePlaylist(playlist: Playlist) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${getString(R.string.do_you_want_delete_playlist)} \"${playlist.playlistName}\"?")
            .setNeutralButton(R.string.no) { dialog, which -> }
            .setPositiveButton(R.string.yes) { dialog, which ->
                playlistSomeViewModel.deletePlaylist()
                findNavController().navigateUp()
            }.show()
    }

    private fun sharePlaylist() {
        playlistSomeViewModel.observeStateTracksInPlaylist().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.no_tracks_to_share,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                playlistSomeViewModel.sharePlaylist()
            }
        }
    }
}