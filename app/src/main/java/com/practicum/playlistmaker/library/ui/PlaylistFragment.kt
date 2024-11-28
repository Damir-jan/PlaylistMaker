package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.MediatekaFragmentDirections
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.ui.state.PlaylistState
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {

        fun newInstance() = PlaylistFragment()
    }

    private val playlistViewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()

    private var adapter: PlaylistAdapter? = null


    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        adapter = PlaylistAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.newPlaylist.setOnClickListener() {
            val action = MediatekaFragmentDirections.actionMediatekaFragmentToNewPlaylistFragment()
            findNavController().navigate(action)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            newPlaylist.isVisible = true
            placeholderImage.isVisible = true
            placeholderImage.isVisible = true
            recyclerView.isVisible = false
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            recyclerView.adapter?.notifyDataSetChanged()

            newPlaylist.isVisible = true
            placeholderImage.isVisible = false
            placeholderImage.isVisible = false
            recyclerView.isVisible = true
        }

        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

}