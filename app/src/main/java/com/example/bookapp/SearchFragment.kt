package com.example.bookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var booksList: ArrayList<BookModel>
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    // this probably doesn't need to be here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // variables for the search
        loadingProgressBar = pb_search
        searchEditText = et_search
        searchButton = btn_search

        // listener for searching
        searchButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE

            if (searchEditText.text.toString().isNullOrEmpty()) {
                searchEditText.error = "Search cannot be blank"
                loadingProgressBar.visibility = View.GONE
            }

            getBooksData(searchEditText.text.toString())
        }
    }

    private fun getBooksData(searchQuery: String) {
        booksList = ArrayList()

        mRequestQueue = Volley.newRequestQueue(this.context)

        mRequestQueue.cache.clear()

        val url = "https://www.googleapis.com/books/v1/volumes?q=$searchQuery&maxResults=30"

        val queue = Volley.newRequestQueue(this.context)

        val request = JsonObjectRequest(Request.Method.GET, url, null, {
                response -> loadingProgressBar.visibility = View.GONE;

            try {
                val itemsArray = response.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val itemsObj = itemsArray.getJSONObject(i)
                    val id = itemsObj.optString("id")
                    val volumeObj = itemsObj.getJSONObject("volumeInfo")
                    val title = volumeObj.optString("title")
                    val subtitle = volumeObj.optString("subtitle")
                    val authorsArray = volumeObj.getJSONArray("authors")
                    val publisher = volumeObj.optString("publisher")
                    val publishedDate = volumeObj.optString("publishedDate")
                    val description = volumeObj.optString("description")
                    val pageCount = volumeObj.optInt("pageCount")
                    val imageLinks = volumeObj.optJSONObject("imageLinks")
                    val thumbnail = imageLinks.optString("thumbnail")
                    val previewLink = volumeObj.optString("previewLink")
                    val infoLink = volumeObj.optString("infoLink")
                    val saleInfoObj = itemsObj.optJSONObject("saleInfo")
                    val buyLink = saleInfoObj.optString("buyLink")
                    val authorsArrayList: ArrayList<String> = ArrayList()
                    for (j in 0 until authorsArray.length()) {
                        authorsArrayList.add(authorsArray.optString(j))
                    }

                    val bookInfo = BookModel(
                        id,
                        title,
                        subtitle,
                        authorsArrayList,
                        publisher,
                        publishedDate,
                        description,
                        pageCount,
                        thumbnail,
                        previewLink,
                        infoLink,
                        buyLink
                    )

                    booksList.add(bookInfo)

                    // val adapter = BookAdapter(booksList, this.context)
                    val adapter = this.context?.let { BookAdapter(booksList, it) }

                    val layoutManager = GridLayoutManager(this.context, 3)
                    val mRecyclerView = rv_books

                    mRecyclerView.layoutManager = layoutManager
                    mRecyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Toast.makeText(this@SearchFragment.context, "No results found", Toast.LENGTH_LONG).show()
        })

        queue.add(request)
    }
}