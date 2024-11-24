package com.weame.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weame.R
import com.weame.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private val articles = mutableListOf<Article>() // Backing list for articles

    // Update the list of articles
    fun updateNews(newArticles: List<Article>) {
        articles.clear() // Clear existing articles
        articles.addAll(newArticles) // Add the new list of articles
        notifyDataSetChanged() // Notify adapter of data change
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article) // Bind the article data to the view holder
    }

    override fun getItemCount(): Int = articles.size // Return the size of the articles list

    // ViewHolder class to bind views
    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.newsTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.newsDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.newsImage)

        // Bind article data to the views
        fun bind(article: Article) {
            titleTextView.text = article.title // Set the title
            descriptionTextView.text = article.description // Set the description

            // Use Glide to load the image
            Glide.with(itemView.context)
                .load(article.urlToImage) // Load the image URL into the ImageView
                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                .error(R.drawable.error_image) // Error image if loading fails
                .into(imageView) // Load the image into the ImageView

            // Set click listener to open article URL in a web browser
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                itemView.context.startActivity(intent)
            }
        }
    }
}