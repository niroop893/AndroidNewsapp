package com.weame.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weame.Article
import com.weame.NewsDetailActivity
import com.weame.R
import com.weame.databinding.ItemNewsBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private var articles = listOf<Article>()

    class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.titleTextView.text = article.title

            // Shorten the URL in description
            val shortUrl = article.url.let {
                if (it.length > 30) "${it.take(30)}..." else it
            }
            binding.descriptionTextView.text = "$shortUrl\n\n${article.description}"

            // Extract image URL from description HTML
            val imageUrl = extractImageUrl(article.description)

            // Load and display the image
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.news_placeholder)
                .error(R.drawable.news_error)
                .into(binding.newsImageView)
        }

        private fun extractImageUrl(description: String): String? {
            val imgPattern = "<img src=\"(.*?)\"".toRegex()
            return imgPattern.find(description)?.groupValues?.get(1)
        }
    }

    fun updateNews(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, NewsDetailActivity::class.java).apply {
                putExtra("url", article.url)
                putExtra("title", article.title)
                putExtra("description", article.description)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = articles.size
}
