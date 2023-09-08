package com.example.bookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.squareup.picasso.Picasso


class BookAdapter(
    private var bookList: ArrayList<BookModel>,
    private var context: Context
    ) : RecyclerView.Adapter<BookAdapter.BookViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : BookViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.book_item,
            parent, false
        )

        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val bookInfo = bookList[position]
        // setting the image from url to the imageview
        val image = bookInfo.thumbnail.subSequence(7, bookInfo.thumbnail.length)
        val link = "https://$image"
        if (bookInfo.thumbnail.isNotEmpty()) {
            Picasso.get().load(link).placeholder(R.drawable.lenny).into(holder.bookImageView)
        }

        // holder.bookImageView.load(bookInfo.thumbnail)
        holder.bookTitleTextView.text = bookInfo.title
        if (bookInfo.authors.size > 0) {
            holder.bookPagesTextView.text = bookInfo.authors[0]
        } else {
            holder.bookPagesTextView.text = "Author not found"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailsActivity::class.java)
            intent.putExtra("id", bookInfo.id)
            intent.putExtra("title", bookInfo.title)
            intent.putExtra("subtitle", bookInfo.subtitle)
            intent.putExtra("authors", bookInfo.authors)
            intent.putExtra("publisher", bookInfo.publisher)
            intent.putExtra("publishedDate", bookInfo.publishedDate)
            intent.putExtra("description", bookInfo.description)
            intent.putExtra("pageCount", bookInfo.pageCount)
            intent.putExtra("thumbnail", bookInfo.thumbnail)
            intent.putExtra("previewLink", bookInfo.previewLink)
            intent.putExtra("infoLink", bookInfo.infoLink)
            intent.putExtra("buyLink", bookInfo.buyLink)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitleTextView: TextView = itemView.findViewById(R.id.tv_book_name)
        val bookPagesTextView: TextView = itemView.findViewById(R.id.tv_pages)
        val bookImageView: ImageView = itemView.findViewById(R.id.iv_book)
    }
}