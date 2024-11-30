package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.MediatekaFragmentDirections
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.library.ui.state.FavoriteState
import com.practicum.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TracksAdapter
import debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteTracksFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        const val CLICK_DEBOUNCE_DELAY = 300L
        const val CLICKED_TRACK: String = "clicked_track"
    }

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel<FavoriteTracksViewModel>()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var openPlayerDebounce: (Track) -> Unit

    private val favoriteTrackClickListener: (Track) -> Unit = { clickedTrack ->
        openPlayerDebounce(clickedTrack)
    }

    private var adapter: TracksAdapter? = null

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var favoriteTracksList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TracksAdapter(favoriteTrackClickListener)

        placeholderMessage = binding.textFavouriteText
        placeholderImage = binding.placeholderImage
        favoriteTracksList = binding.favoriteTracksList

        favoriteTracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoriteTracksList.adapter = adapter

        favoriteTracksViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        openPlayerDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { clickedTrack ->
            val action =
                MediatekaFragmentDirections.actionMediatekaFragmentToPlayerFragment(clickedTrack)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
        favoriteTracksList.adapter = null
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.favoriteTracks)
            is FavoriteState.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        favoriteTracksList.isVisible = false
        placeholderMessage.isVisible = true
        placeholderImage.isVisible = true

        placeholderMessage.text = message
    }

    private fun showContent(favoriteTracks: List<Track>) {
        favoriteTracksList.isVisible = true
        placeholderImage.isVisible = false
        placeholderMessage.isVisible = false

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(favoriteTracks)
        adapter?.notifyDataSetChanged()  // или используйте adapter?.submitList(favoriteTracks)
    }
}
