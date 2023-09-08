package com.example.bookapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_book_details.*

class BookDetailsFragment : Fragment() {
    lateinit var titleTextView: TextView
    lateinit var subtitleTextView: TextView
    lateinit var publisherTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var pageTextView: TextView
    lateinit var publishDateTextView: TextView
    lateinit var previewButton: Button
    lateinit var addButton: Button
    lateinit var removeButton: Button
    lateinit var bookImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_details, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView = tv_title
        subtitleTextView = tv_subtitle
        publisherTextView = tv_publisher
        descriptionTextView = tv_description
        pageTextView = tv_number_of_pages
        publishDateTextView = tv_publish_date
        previewButton = btn_preview
        addButton = btn_add_to_list
        removeButton = btn_remove_from_list
        bookImageView = iv_book

        // getting data from the intent that sent us here
        val bookid = activity?.intent?.getStringExtra("id")
        val title = activity?.intent?.getStringExtra("title")
        val subtitle = activity?.intent?.getStringExtra("subtitle")
        val publisher = activity?.intent?.getStringExtra("publisher")
        val publishedDate = activity?.intent?.getStringExtra("publishedDate")
        val description = activity?.intent?.getStringExtra("description")
        val pageCount = activity?.intent?.getIntExtra("pageCount", 0)
        val thumbnail = activity?.intent?.getStringExtra("thumbnail")
        val previewLink = activity?.intent?.getStringExtra("previewLink")
        val infoLink = activity?.intent?.getStringExtra("infoLink")
        val buyLink = activity?.intent?.getStringExtra("buyLink")

        // setting textviews and picasso setting the image
        titleTextView.text = title
        subtitleTextView.text = subtitle
        publisherTextView.text = publisher
        publishDateTextView.text = "Published on: " + publishedDate
        descriptionTextView.text = description
        pageTextView.text = "No of pages: " + pageCount
        val picasso = Picasso.get()
        picasso.load(thumbnail).into(bookImageView)

        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val userid = user?.uid

        val query = db.collection("books")
            .whereEqualTo("userID", userid)
            .whereEqualTo("bookID", bookid)
        val count = query.count()

        count.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.count.toInt() >= 1) {
                    removeButton.visibility = View.VISIBLE
                    addButton.visibility = View.GONE
                } else {
                    removeButton.visibility = View.GONE
                    addButton.visibility = View.VISIBLE
                }
            }
        }

        previewButton.setOnClickListener {
            if (previewLink.isNullOrEmpty()) {
                Toast.makeText(
                    this.context,
                    "No preview link available",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val uri: Uri = Uri.parse(previewLink)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // queries the db to determine if the user has already added the book
        removeButton.setOnClickListener {
            db.collection("books")
                .whereEqualTo("userID", userid)
                .whereEqualTo("bookID", bookid)
                .get()
                .addOnSuccessListener { result ->
                    db.collection("books")
                        .document(result.documents[0].id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this.context,
                                "Book successfully removed from list",
                                Toast.LENGTH_SHORT
                            ).show()
                            removeButton.visibility = View.GONE
                            addButton.visibility = View.VISIBLE
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this.context,
                                "An error occurred while removing book",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this.context,
                        "Book was not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }

        addButton.setOnClickListener {
            val document = hashMapOf(
                "userID" to userid,
                "bookID" to bookid,
                "title" to title,
                "subtitle" to subtitle,
                "publisher" to publisher,
                "publishDate" to publishedDate,
                "description" to description,
                "pageCount" to pageCount,
                "thumbnail" to thumbnail,
                "previewLink" to previewLink
            )

            db.collection("books")
                .add(document)
                .addOnSuccessListener {
                    Toast.makeText(
                        this.context,
                        "Book successfully added to reading list",
                        Toast.LENGTH_LONG
                    ).show()

                    removeButton.visibility = View.VISIBLE
                    addButton.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this.context,
                        "An error occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }


        }
    }
}