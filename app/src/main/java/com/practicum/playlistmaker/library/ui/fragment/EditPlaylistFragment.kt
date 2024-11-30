package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.Playlist
import com.practicum.playlistmaker.library.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private val args: EditPlaylistFragmentArgs by navArgs()

    override val playlistViewModel : EditPlaylistViewModel by viewModel() {
        parametersOf(args.playlist)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.getPlaylistById()
        playlistViewModel.observeStatePlaylist().observe(viewLifecycleOwner) { playlist ->
            setOldPlaylistData(playlist)
        }

        with(binding) {
            newPlaylist.text = getString(R.string.editPlaylist)
            createPlaylist.text = getString(R.string.save)
        }

        binding.createPlaylist.setOnClickListener() {
            playlistViewModel.editPlaylist()
            findNavController().navigateUp()
            Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed(
        playlistNameEditText: TextInputEditText,
        playlistDescriptionEditText: TextInputEditText
    ) {
        findNavController().navigateUp()
    }

    private fun setOldPlaylistData(playlist: Playlist) {
        playlistNameEditText?.setText(playlist.playlistName)
        playlistDescriptionEditText?.setText(playlist.playlistDescription)
        if (playlist.uri.isNullOrEmpty()) {
            binding.playlistImage.setImageResource(R.drawable.placeholder)
        } else {
            binding.playlistImage.setImageURI(playlist.uri.toUri())
        }
    }

}