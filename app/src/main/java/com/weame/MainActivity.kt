package com.weame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.weame.databinding.ActivityMainBinding
import com.weame.adapter.NewsAdapter
import com.weame.models.Article // Use standalone Article class
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val defaultCategory = "general"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupNavigationDrawer()
        setupRecyclerView()

        // Always load general news first
        fetchNews(defaultCategory)

        // Set the general news item as selected in the navigation drawer
        navView.setCheckedItem(R.id.nav_general)
    }

    private fun setupViews() {
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        recyclerView = binding.recyclerView

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            title = getString(R.string.app_name)
        }
    }

    private fun setupNavigationDrawer() {
        navView.setNavigationItemSelectedListener { menuItem ->
            val category = when (menuItem.itemId) {
                R.id.nav_general -> "general"
                R.id.nav_sports -> "sports"
                R.id.nav_science -> "science"
                R.id.nav_politics -> "politics"
                R.id.nav_entertainment -> "entertainment"
                else -> defaultCategory
            }
            fetchNews(category)
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter()
        recyclerView.adapter = newsAdapter
    }

    private fun shortenNewsUrl(url: String): String {
        return url.split("&url=").lastOrNull()?.split("&")?.firstOrNull() ?: url
    }

    private fun fetchNews(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://news.google.com/rss/search?q=${category.uppercase()}&hl=en-IN&gl=IN&ceid=IN:en")
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(url.openStream(), "UTF-8")
                var eventType = parser.eventType
                var title = ""
                var link = ""
                var description = ""
                var imageUrl = ""
                var publishedAt = "" // Initialize publishedAt
                var source = "Google News" // Default source name
                val articles = mutableListOf<com.weame.models.Article>()

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (parser.name) {
                                "item" -> {
                                    title = ""
                                    link = ""
                                    description = ""
                                    imageUrl = ""
                                    publishedAt = "" // Reset for each item
                                    source = "Google News" // Reset source for each item, or extract if available
                                }
                                "title" -> title = parser.nextText()
                                "link" -> link = parser.nextText()
                                "description" -> description = parser.nextText()
                                "media:content" -> {
                                    imageUrl = parser.getAttributeValue(null, "url") ?: ""
                                }
                                "pubDate" -> publishedAt = parser.nextText() // Extract published date
                                // If your RSS feed includes a source tag, extract it here
                                // "source" -> source = parser.nextText() // Uncomment if a source tag exists
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (parser.name == "item" && title.isNotEmpty()) {
                                articles.add(
                                    com.weame.models.Article(
                                        title = title,
                                        description = description,
                                        url = shortenNewsUrl(link),
                                        urlToImage = imageUrl,
                                        publishedAt = publishedAt, // Pass the published date
                                        source = source // Pass the source
                                    )
                                )
                                Log.d("NewsParser", "Added article: $title, Image URL: $imageUrl, Published At: $publishedAt, Source: $source")
                            }
                        }
                    }
                    eventType = parser.next()
                }
                launch(Dispatchers.Main) {
                    Log.d("NewsParser", "Total articles: ${articles.size}")
                    newsAdapter.updateNews(articles)
                }
            } catch (e: Exception) {
                Log.e("NewsParser", "Error fetching news", e)
            }
        }
    }




    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
