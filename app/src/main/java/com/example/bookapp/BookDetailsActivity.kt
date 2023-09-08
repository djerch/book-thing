package com.example.bookapp

import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class BookDetailsActivity : AppCompatActivity() {
    lateinit var authorsTextView: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        window.setBackgroundDrawableResource(R.drawable.books3)

        authorsTextView = findViewById(R.id.tv_authors)
        titleTextView = findViewById(R.id.tv_title)
        subtitleTextView = findViewById(R.id.tv_subtitle)
        publisherTextView = findViewById(R.id.tv_publisher)
        descriptionTextView = findViewById(R.id.tv_description)
        pageTextView = findViewById(R.id.tv_number_of_pages)
        publishDateTextView = findViewById(R.id.tv_publish_date)
        previewButton = findViewById(R.id.btn_preview)
        addButton = findViewById(R.id.btn_add_to_list)
        removeButton = findViewById(R.id.btn_remove_from_list)
        bookImageView = findViewById(R.id.iv_book_details)

        // getting data from the intent that sent us here
        val bookid = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("subtitle")
        val publisher = intent.getStringExtra("publisher")
        val publishedDate = intent.getStringExtra("publishedDate")
        val description = intent.getStringExtra("description")
        val pageCount = intent.getIntExtra("pageCount", 0)
        val thumbnail = intent.getStringExtra("thumbnail")
        val previewLink = intent.getStringExtra("previewLink")
        val authors = intent.getStringArrayListExtra("authors")
        val infoLink = intent.getStringExtra("infoLink")
        val buyLink = intent.getStringExtra("buyLink")

        // building an authors string
        var authorString = ""
        if (authors != null) {
            if (authors.size > 1) {
                var first = true
                for (author in authors) {
                    if (first) {
                        authorString = author
                        first = false
                    } else {
                        authorString = authorString + "\n" + author
                    }
                }
            } else {
                authorString = authors[0]
            }
        }

        // setting textviews and picasso setting the image
        authorsTextView.text = authorString
        titleTextView.text = title
        subtitleTextView.text = subtitle
        publisherTextView.text = publisher
        publishDateTextView.text = "Published: " + publishedDate
        descriptionTextView.text = description
        pageTextView.text = "Pages: " + pageCount
        val image = thumbnail?.subSequence(7, thumbnail.length)
        val link = "https://$image"
        Picasso.get().load(link).placeholder(R.drawable.lenny).into(bookImageView)

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
                    this@BookDetailsActivity,
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
                                this@BookDetailsActivity,
                                "Book successfully removed from list",
                                Toast.LENGTH_SHORT
                            ).show()
                            removeButton.visibility = View.GONE
                            addButton.visibility = View.VISIBLE
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@BookDetailsActivity,
                                "An error occurred while removing book",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@BookDetailsActivity,
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
                "authors" to authors,
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
                        this,
                        "Book successfully added to reading list",
                        Toast.LENGTH_LONG
                    ).show()

                    removeButton.visibility = View.VISIBLE
                    addButton.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "An error occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }


        }
    }
}