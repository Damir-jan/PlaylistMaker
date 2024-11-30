package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private lateinit var toastPlaylistName: String

    private val newPlaylistVewModel by viewModel<NewPlaylistViewModel>()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var isImageAdd: Boolean = false

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistNameEditText =
            binding.playlistName.findViewById<TextInputEditText>(R.id.playlistLayout)

        val playlistDescriptionEditText =
            binding.playlistDescription.findViewById<TextInputEditText>(R.id.playlistDescription)

        playlistNameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylist.isEnabled = !text.isNullOrBlank()


            newPlaylistVewModel.setPlaylistName(text.toString())
            toastPlaylistName = text.toString()
        }

        playlistDescriptionEditText.doOnTextChanged { text, _, _, _ ->
            newPlaylistVewModel.setPlaylistDescroption(text.toString())
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.playlistImage.setImageURI(uri)
                newPlaylistVewModel.saveImageToLocalStorage(uri)
                newPlaylistVewModel.setUri(uri)
                isImageAdd = true
            } else {

            }
        }


        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which -> }
            .setPositiveButton("Завершить") { dialog, which ->
                findNavController().navigateUp()
            }

        binding.playlistImage.setOnClickListener() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.back.setOnClickListener() {
            onBackPressed(playlistNameEditText, playlistDescriptionEditText)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed(playlistNameEditText, playlistDescriptionEditText)
                }
            })

        binding.createPlaylist.setOnClickListener() {
            newPlaylistVewModel.createPlaylist()
            findNavController().navigateUp()
            Toast.makeText(requireContext(), "Плейлист $toastPlaylistName создан", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackPressed(playlistNameEditText : TextInputEditText, playlistDescriptionEditText : TextInputEditText) {
        if (isImageAdd || !playlistNameEditText.text.isNullOrBlank() || !playlistDescriptionEditText.text.isNullOrBlank()) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }
}