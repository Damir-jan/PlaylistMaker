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

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var updateButton: Button
    private val adapter = TracksAdapter()


    @SuppressLint("WrongViewCast", "MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)



        val set_button = findViewById<ImageButton>(R.id.Back)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        inputEditText = findViewById(R.id.Search_line)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        updateButton = findViewById(R.id.updateButton)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
            }
            false
        }

        set_button.setOnClickListener {
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

    fun searchTrack() {
        val call = itunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<SearchResponse> {        //тут вызываешь метод который указан у тебя в api активити и передаешь в него экземпляр класса editText из которого ты берешь сам текст

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>,
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true){
                            tracksList.clear()
                            tracksList.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                            showMessage("", "")
                            showButton(false)
                        } else {
                            showMessage("Ничего не нашлось","")
                            showButton(false)
                            Log.d("y", response.body()?.results.toString())
                        }
                    } else {
                        showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                        showButton(true)
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                    showButton(true)
                }
            })
    }

    private fun showButton(show: Boolean) {
        if (show) {
            updateButton.visibility = View.VISIBLE
        } else
            updateButton.visibility = View.GONE
    }



    /* private fun searchTrack(searchText : String) {
         itunesService.search(searchText)
             .enqueue(object : Callback<SearchResponse> {
                 @SuppressLint("NotifyDataSetChanged")

                 override fun onResponse(
                     call: Call<SearchResponse>,
                     response: Response<SearchResponse>
                 ) {

                     if (response.isSuccessful) {
                         val trackResponse = response.body()
                         if (trackResponse!=null && trackResponse.results.isNotEmpty()) {
                             tracks.clear()
                             tracks.addAll(trackResponse.results)
                             recView.adapter?.notifyDataSetChanged()
                             showMessage("")
                         } else {
                             showMessage(getString(R.string.nothingToFind))
                             placeholderImage.visibility = View.VISIBLE
                             placeholderImage.setImageResource(R.drawable.nothing_to_find)
                             updateButton.visibility = View.GONE
                         }
                     } else {
                         showMessage(getString(R.string.noInternetError))
                         placeholderImage.visibility = View.VISIBLE
                         updateButton.visibility = View.VISIBLE
                         placeholderImage.setImageResource(R.drawable.no_internet_error)
                     }
                 }

                 @SuppressLint("NotifyDataSetChanged")
                 override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                     showMessage(getString(R.string.noInternetError))
                     tracks.clear()
                     recView.adapter?.notifyDataSetChanged()
                     placeholderImage.visibility = View.VISIBLE
                     updateButton.visibility = View.VISIBLE
                    // placeholderImage.setImageResource(R.drawable.something_went_wrong)
                 }
             })
     }

                /*     if (response.code() == 200) {
                         tracks.clear()
                         val results = response.body()?.results ?: listOf()
                         if (results.isNotEmpty()) {
                             tracks.addAll(results)
                             //adapter.notifyDataSetChanged()//если ответ не пустой то добавляешь их в лист
                         }
                         if (tracks.isEmpty() && inputEditText.text.isNotEmpty()) {
                             showMessage(getString(R.string.nothingToFind))
                         } else {
                             showMessage("")
                         }
                     }
                 }
                 override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                     showMessage(getString(R.string.noInternetError))
                 }
             })
     } */*/



    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {

            placeholderMessage.visibility = View.VISIBLE
            tracksList.clear()
            adapter.notifyDataSetChanged()
            recView.adapter?.notifyDataSetChanged()
            placeholderMessage.text = text
        } else {
            placeholderMessage.visibility = View.GONE
        }
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



