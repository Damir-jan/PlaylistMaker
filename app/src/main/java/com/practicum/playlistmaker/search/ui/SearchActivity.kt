
package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.player.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TracksAdapter
import com.practicum.playlistmaker.search.ui.models.SearchTracksState
import com.practicum.playlistmaker.search.view_model.SearchTracksViewModel


class SearchActivity : AppCompatActivity() {

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val CLICKED_TRACK: String = "clicked_track"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchTracksViewModel
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true
   // private var textWatcher: TextWatcher? = null

    private lateinit var searchAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter


    private lateinit var progressBar: ProgressBar




    private val historyTrackClickListener: (Track) -> Unit =
        { clickedTrack ->
            openPlayer(clickedTrack)
        }

    private val currentTrackClickListener: (Track) -> Unit =
        { clickedTrack ->
            openPlayer(clickedTrack)
        }


    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchTracksViewModel.getViewModelFactory()
        )[SearchTracksViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        progressBar = findViewById(R.id.progressBar)
        //val setButton = findViewById<ImageButton>(R.id.back)
        //val clearButton = findViewById<ImageView>(R.id.clearIcon)
        //inputEditText = findViewById(R.id.search_line)

        searchAdapter = TracksAdapter(currentTrackClickListener)
        historyAdapter = TracksAdapter(historyTrackClickListener)

        binding.dataTracks.adapter = searchAdapter
        binding.tracksHistoryList.adapter = historyAdapter

        binding.dataTracks.layoutManager = LinearLayoutManager(this)
        binding.tracksHistoryList.layoutManager = LinearLayoutManager(this)



        binding.searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTrack(binding.searchLine.text.toString())
            }
            false
        }

        binding.searchLine.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchLine.text.isEmpty()) {
                val historyTracks = viewModel.readTracksFromHistory().toMutableList()
                historyAdapter.setData(historyTracks)
                binding.historyLayout.visibility =
                    if (historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
            } else {
                binding.historyLayout.visibility = View.GONE
            }
        }


        binding.back.setOnClickListener {
            finish()
        }


        binding.clearIcon.setOnClickListener {
            binding.searchLine.setText("")
            binding.searchLine.clearFocus()
            searchAdapter.tracks = arrayListOf()
            hidePlaceholdersAndUpdateBtn()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
                    val searchText = s?.toString() ?: ""
                    searchAdapter.tracks.clear()
                    binding.dataTracks.adapter?.notifyDataSetChanged()
                    hidePlaceholdersAndUpdateBtn()
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

        /*val sharedPreferences = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        recView.adapter = TracksAdapter(tracksList, currentTrackClickListener)


        tracksHistoryList = findViewById(R.id.tracksHistoryList)
        tracksHistoryList.layoutManager = LinearLayoutManager(this)

        val historyTracks = searchHistory.readTracks().toMutableList()

        historyAdapter = TracksAdapter(historyTracks, historyTrackClickListener)
        tracksHistoryList.adapter = historyAdapter

        tracksHistoryList.adapter?.notifyDataSetChanged()

        cleanHistoryButton = findViewById(R.id.cleanHistory)
        cleanHistoryButton.setOnClickListener {
            historyTracks.clear()
            sharedPreferences.edit().remove(HISTORY_TRACKS_LIST_KEY).apply()
            historyLayout.visibility = View.GONE
            tracksHistoryList.adapter?.notifyDataSetChanged()
        }
*/

    }


    private fun openPlayer(clickedTrack: Track) {
        if (clickDebounce()) {
            viewModel.onClickedTrack(listOf(clickedTrack))   // странно как-то, что я передаю здесь List, а не track = ?
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(CLICKED_TRACK, clickedTrack)
            startActivity(playerIntent)
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
            is SearchTracksState.Empty -> showEmpty(state.message)
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

            placeholderImage.setImageResource(R.drawable.nothing_to_find)
            placeholderMessage.text = errorMessage
        }
    }

    private fun showEmpty(emptyMessage: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            historyLayout.visibility = View.GONE
            updateButton.visibility = View.GONE
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE

            placeholderImage.setImageResource(R.drawable.nothing_to_find)
            placeholderMessage.text = emptyMessage
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
            binding.youSearch.visibility = View.GONE
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
}









