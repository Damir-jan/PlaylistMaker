package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TracksAdapter
import com.practicum.playlistmaker.search.ui.models.SearchTracksState
import com.practicum.playlistmaker.search.view_model.SearchTracksViewModel
import debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val CLICKED_TRACK: String = "clicked_track"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private var isFocused: Boolean = false

    private lateinit var onMusicClickDebounce: (Track) -> Unit

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchTracksViewModel>()
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    private val searchAdapter: TracksAdapter by lazy {
        TracksAdapter { clickedTrack -> openPlayer(clickedTrack) }
    }

    private val historyAdapter: TracksAdapter by lazy {
        TracksAdapter { clickedTrack -> openPlayer(clickedTrack) }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }


        binding.dataTracks.adapter = searchAdapter
        binding.tracksHistoryList.adapter = historyAdapter

        binding.dataTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksHistoryList.layoutManager = LinearLayoutManager(requireContext())

        //binding.historyLayout.visibility = View.GONE
        hideHistory()

        binding.searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTrack(binding.searchLine.text.toString())
            }
            false
        }

        onMusicClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { clickedTrack ->
            viewModel.onClickedTrack(listOf(clickedTrack))
            historyAdapter.notifyDataSetChanged()
            val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(clickedTrack)
            findNavController().navigate(action)
        }

        binding.searchLine.setOnFocusChangeListener { _, hasFocus ->
            isFocused = hasFocus
            if (hasFocus && binding.searchLine.text.isEmpty()) {
                viewModel.loadHistory()

            } else {
            hideHistory()
            }
        }




        binding.clearIcon.setOnClickListener {
            binding.searchLine.setText("")
            binding.searchLine.clearFocus()
            searchAdapter.tracks.clear()
            binding.dataTracks.adapter?.notifyDataSetChanged()
            //searchAdapter.tracks = arrayListOf()
            viewModel.loadHistory()
            hidePlaceholdersAndUpdateBtn()
            val imm = requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchLine.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                binding.historyLayout.visibility =
                    if (binding.searchLine.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {

                if (s.isNullOrEmpty()) {
                    searchAdapter.tracks.clear()
                    binding.dataTracks.adapter?.notifyDataSetChanged()
                    hidePlaceholdersAndUpdateBtn()
                    viewModel.loadHistory()
                }
            }
        }

        binding.searchLine.addTextChangedListener(simpleTextWatcher)
        savedInstanceState?.getString(SEARCH_TEXT)
            ?.let {
                binding.searchLine.setText(it)
            }

        binding.updateButton.setOnClickListener {
            viewModel.searchTrack(binding.searchLine.text.toString())
        }

        binding.cleanHistory.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearHistory()
                historyAdapter.setData(emptyList())
                binding.historyLayout.visibility = View.GONE
                historyAdapter.notifyDataSetChanged()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchLine.removeTextChangedListener(it) }
    }


    private fun openPlayer(clickedTrack: Track) {
        if (clickDebounce()) {
            viewModel.onClickedTrack(listOf(clickedTrack))


            val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(clickedTrack)
            findNavController().navigate(action)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private fun render(state: SearchTracksState) {
        when (state) {
            is SearchTracksState.Loading -> showLoading()
            is SearchTracksState.Content -> {
                searchAdapter.tracks = state.tracks
                showContent()
            }

            is SearchTracksState.Error -> showError(state.errorMessage)
            is SearchTracksState.Empty -> showEmpty()
            is SearchTracksState.History -> {
                if (isFocused) {
                    updateHistoryList(state.tracks)
                    showHistory(state.tracks)
                    searchAdapter.tracks.clear()
                    binding.dataTracks.adapter?.notifyDataSetChanged()

                }
            }

            else -> {}
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            historyLayout.visibility = View.GONE
            updateButton.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            historyLayout.visibility = View.GONE
            updateButton.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE

            placeholderImage.setImageResource(R.drawable.no_internet_error)
            placeholderMessage.text = errorMessage
        }
    }

    private fun showEmpty() {
        with(binding) {
            progressBar.visibility = View.GONE
            historyLayout.visibility = View.GONE
            updateButton.visibility = View.GONE
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.setImageResource(R.drawable.nothing_to_find)
            placeholderMessage.setText(R.string.nothing_to_find)
        }
    }

    private fun showContent() {
        with(binding) {
            dataTracks.adapter?.notifyDataSetChanged()

            progressBar.visibility = View.GONE
            historyLayout.visibility = View.GONE
            updateButton.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
        }
        hideHistory()
    }

    private fun hideHistory() {
        with(binding) {
            tracksHistoryList.visibility = View.GONE
            youSearch.visibility = View.GONE
            cleanHistory.visibility = View.GONE
        }
    }

    private fun hidePlaceholdersAndUpdateBtn() {
        with(binding) {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showHistory(historyTracks: MutableList<Track>) {
        with(binding) {
            tracksHistoryList.visibility = View.VISIBLE
            youSearch.visibility = View.VISIBLE
            cleanHistory.visibility = View.VISIBLE
            binding.historyLayout.isVisible = historyTracks.isNotEmpty()
        }
    }

    private fun updateHistoryList(historyTracks: MutableList<Track>) {
        historyAdapter.setData(historyTracks)
        historyAdapter.notifyDataSetChanged()
    }


}











