package com.example.bookapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_reading_list.*

class ReadingListFragment : Fragment() {
    private lateinit var bookList: ArrayList<BookModel>
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reading_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = pb_search
        loadingProgressBar.visibility = View.VISIBLE
        getBooksData()
    }

    private fun getBooksData() {

        bookList = ArrayList()

        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val userid = user?.uid

        db.collection("books")
            .whereEqualTo("userID", userid)
            .get()
            .addOnSuccessListener { result ->
                Toast.makeText(
                    this.context,
                    "retrieved data",
                    Toast.LENGTH_LONG
                ).show()

                try {
                    for (doc in result) {
                        val id = doc.data["bookID"].toString()
                        val title = doc.data["title"].toString()
                        val subtitle = doc.data["subtitle"].toString()
                        val publisher = doc.data["publisher"].toString()
                        val publishedDate = doc.data["publishedDate"].toString()
                        val description = doc.data["description"].toString()
                        val pageCount = doc.data["pageCount"].toString()
                        val thumbnail = doc.data["thumbnail"].toString()
                        val previewLink = doc.data["previewLink"].toString()
                        val authorsArray = doc.data["authors"] as ArrayList<String>
                        val infoLink = "link"
                        val buyLink = "link"

                        val bookInfo = BookModel(
                            id,
                            title,
                            subtitle,
                            authorsArray,
                            publisher,
                            publishedDate,
                            description,
                            pageCount.toInt(),
                            thumbnail,
                            previewLink,
                            infoLink,
                            buyLink
                        )

                        bookList.add(bookInfo)

                        val adapter = this.context?.let { BookAdapter(bookList, it) }

                        val layoutManager = GridLayoutManager(this.context, 3)
                        val mRecyclerView = rv_books

                        mRecyclerView.layoutManager = layoutManager
                        mRecyclerView.adapter = adapter
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                    Toast.makeText(
//                        this@ReadingListActivity,
//                        "$e",
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this.context,
                    "There was an error retrieving data",
                    Toast.LENGTH_LONG
                ).show()
            }

    }
}