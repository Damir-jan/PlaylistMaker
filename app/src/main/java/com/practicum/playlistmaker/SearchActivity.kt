package com.practicum.playlistmaker

import Track
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
    private lateinit var progressBar: ProgressBar


    private val SEARCH_HISTORY_PREFERENCES = "practicum_example_preferences"

    private var inputText: String? = null

    private val iTunesBaseUrl = "https://itunes.apple.com"


    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)


    private lateinit var recView: RecyclerView
    private val tracksList = mutableListOf<Track>()

    private lateinit var placeHolderMessage: TextView
    private lateinit var placeHolderImage: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var historyAdapter: TracksAdapter

    private lateinit var tracksHistoryList: RecyclerView
    private lateinit var cleanHistoryButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var searchHistory: SearchHistory

    private val historyTrackClickListener: (Track) -> Unit = { clickedTrack ->
        val playerIntent = Intent(this,PlayerActivity::class.java)
        playerIntent.putExtra("clicked_track", Gson().toJson(clickedTrack))
        startActivity(playerIntent)
    }

    private val currentTrackClickListener: (Track) -> Unit = { clickedTrack ->
        searchHistory.saveTrack(listOf(clickedTrack))
        val playerIntent = Intent(this,PlayerActivity::class.java)
        playerIntent.putExtra("clicked_track", Gson().toJson(clickedTrack))
        startActivity(playerIntent)
    }



    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        progressBar = findViewById(R.id.progressBar)
        val setButton = findViewById<ImageButton>(R.id.back)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        inputEditText = findViewById(R.id.search_line)
        this.placeHolderMessage = findViewById(R.id.placeholderMessage)
        this.placeHolderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)
        historyLayout = findViewById(R.id.historyLayout)

        historyLayout.visibility = View.GONE

        recView = findViewById(R.id.dataTracks)
        recView.layoutManager = LinearLayoutManager(this)



        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

        inputEditText.setOnFocusChangeListener { _ , hasFocus ->
            val historyTracks = searchHistory.readTracks().toMutableList()
            if (historyTracks.isEmpty()) {
                historyLayout.visibility = View.GONE
                cleanHistoryButton.visibility = View.GONE
            } else {
                historyAdapter = TracksAdapter(historyTracks, historyTrackClickListener)
                tracksHistoryList.adapter = historyAdapter
                tracksHistoryList.adapter?.notifyDataSetChanged()
                historyLayout.visibility = View.VISIBLE
                cleanHistoryButton.visibility = View.VISIBLE
            }
        }


        setButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()
            hideKeyboard()
            tracksList.clear()
            recView.adapter?.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
                historyLayout.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString() ?: ""
                clearButton.visibility = clearButtonVisibility(searchText)

                if (searchText.isEmpty()) {
                    recView.visibility = View.GONE
                    historyLayout.visibility = View.VISIBLE
                    cleanHistoryButton.visibility = View.VISIBLE
                } else {
                    recView.visibility = View.VISIBLE
                    historyLayout.visibility = View.GONE
                    cleanHistoryButton.visibility = View.GONE
                }
            }

        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString("inputText")
            inputEditText.setText(inputText)
        }

        updateButton.setOnClickListener {
            searchTrack()
        }

        val sharedPreferences = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)
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


    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTrack() {

        placeHolderMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        historyLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE


        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<SearchResponse> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>,
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        Log.d("Search", response.body()?.results.toString())
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracksList.clear()
                            tracksList.addAll(response.body()?.results!!)
                            recView.adapter?.notifyDataSetChanged()
                            showPlaceHolder(null)
                            showMessage("", "")
                            showButton(false)
                            historyLayout.visibility = View.GONE
                        } else {
                            showPlaceHolder(PlaceHolderPicture.NothingToFind)
                            showMessage("Ничего не нашлось", "")
                            showButton(false)
                            historyLayout.visibility = View.GONE
                            Log.d("y", response.body()?.results.toString())
                        }
                    } else {
                        showPlaceHolder(PlaceHolderPicture.NoInternet)
                        showMessage(
                            "Проблемы со связью",
                            "Загрузка не удалась. Проверьте подключение к интернету"
                        )
                        showButton(true)
                        historyLayout.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showPlaceHolder(PlaceHolderPicture.NoInternet)
                    showMessage(
                        "Проблемы со связью",
                        "Загрузка не удалась. Проверьте подключение к интернету"
                    )
                    showButton(true)
                    historyLayout.visibility = View.GONE
                }
            })
    }

    private fun showButton(show: Boolean) {
        if (show) {
            updateButton.visibility = View.VISIBLE
        } else {
            updateButton.visibility = View.GONE
        }
    }

    private fun showPlaceHolder(picture: PlaceHolderPicture?) {
        if (picture != null) {
            placeHolderImage.setImageResource(picture.resourceId)
            placeHolderImage.visibility = View.VISIBLE
        } else {
            placeHolderImage.visibility = View.GONE
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            placeHolderMessage.visibility = View.VISIBLE
            tracksList.clear()
            historyAdapter.notifyDataSetChanged()
            placeHolderMessage.text = text
            if (additionalText.isNotEmpty()) {
                placeHolderMessage.text = "$text\n\n$additionalText"
            }
        } else {
            placeHolderMessage.visibility = View.GONE
        }
    }

    enum class PlaceHolderPicture(val resourceId: Int) {
        NothingToFind(R.drawable.nothing_to_find),
        NoInternet(R.drawable.no_internet_error)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("inputText", inputText)
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}



