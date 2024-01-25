package com.practicum.playlistmaker

import Track
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class SearchActivity : AppCompatActivity() {

    private var inputText: String? = null

    private val iTunesBaseUrl = "https://itunes.apple.com"


    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())

        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)


    private lateinit var recView: RecyclerView
    private val tracksList = ArrayList<Track>()

    private lateinit var placeHolderMessage: TextView
    private lateinit var placeHolderImage: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var updateButton: Button
    private val adapter = TracksAdapter(tracksList)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)



        val setButton = findViewById<ImageButton>(R.id.Back)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        inputEditText = findViewById(R.id.Search_line)
        this.placeHolderMessage = findViewById(R.id.placeholderMessage)
        this.placeHolderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

        setButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)


            }

            override fun afterTextChanged(s: Editable?) {

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

        recView = findViewById(R.id.DataTracks)
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(this)


    }

    private fun searchTrack() {
        itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<SearchResponse> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>,
                ) {
                    if (response.code() == 200) {
                        Log.d("Search", response.body()?.results.toString())
                        if (response.body()?.results?.isNotEmpty() == true){
                            tracksList.clear()
                            tracksList.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            showPlaceHolder(null)
                            showMessage("", "")
                            showButton(false)
                        } else {
                            showPlaceHolder(PlaceHolderPicture.NothingToFind)
                            showMessage("Ничего не нашлось","")
                            showButton(false)
                            Log.d("y", response.body()?.results.toString())
                        }
                    } else {
                        showPlaceHolder(PlaceHolderPicture.NoInternet)
                        showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                        showButton(true)
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showPlaceHolder(PlaceHolderPicture.NoInternet)
                    showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                    showButton(true)
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
    fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            placeHolderMessage.visibility = View.VISIBLE
            tracksList.clear()
            adapter.notifyDataSetChanged()
            placeHolderMessage.text = text
            if (additionalText.isNotEmpty()){
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
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }

            }



