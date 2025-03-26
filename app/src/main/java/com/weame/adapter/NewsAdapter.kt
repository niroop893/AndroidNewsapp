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
import com.weame.Article
import com.weame.NewsDetailActivity
import com.weame.R

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private val articles = mutableListOf<Article>()

    fun updateNews(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.newsTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.newsDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.newsImage)

        fun bind(article: Article) {
            titleTextView.text = article.title
            descriptionTextView.text = article.description

            Glide.with(itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, NewsDetailActivity::class.java)
                intent.putExtra("url", article.url)
                itemView.context.startActivity(intent)
            }
        }
    }
}
