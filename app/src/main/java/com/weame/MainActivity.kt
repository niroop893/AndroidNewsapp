package com.weame

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.weame.adapter.NewsAdapter
import com.weame.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private val defaultCategory = "general"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            title = getString(R.string.app_name)
        }

        // Setup Drawer and Navigation
        setupNavigationDrawer()

        // Setup RecyclerView
        setupRecyclerView()

        // Load general news by default
        fetchNews(defaultCategory)

        // Set default selected navigation item
        binding.navView.setCheckedItem(R.id.nav_general)
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            val category = when (menuItem.itemId) {
                R.id.nav_general -> "general"
                R.id.nav_sports -> "sports"
                R.id.nav_science -> "science"
                R.id.nav_politics -> "politics"
                R.id.nav_entertainment -> "entertainment"
                else -> defaultCategory
            }
            fetchNews(category)
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }
    }

    private fun fetchNews(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val encodedCategory = URLEncoder.encode(category, "UTF-8")
                val url = "https://news.google.com/rss/search?q=$encodedCategory&hl=en-IN&gl=IN&ceid=IN:en"
                Log.d("NewsFetch", "Fetching news from: $url")

                val connection = URL(url).openConnection()
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(connection.getInputStream(), "UTF-8")

                val articles = parseRss(parser)

                launch(Dispatchers.Main) {
                    Log.d("NewsFetch", "Total Articles: ${articles.size}")
                    newsAdapter.updateNews(articles)
                }
            } catch (e: Exception) {
                Log.e("NewsFetch", "Error fetching news", e)
            }
        }
    }

    private fun parseRss(parser: XmlPullParser): List<Article> {
        val articles = mutableListOf<Article>()
        var eventType = parser.eventType
        var title = ""
        var link = ""
        var description = ""
        var imageUrl = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "item" -> {
                        title = ""
                        link = ""
                        description = ""
                        imageUrl = ""
                    }
                    "title" -> title = parser.nextText()
                    "link" -> link = parser.nextText()
                    "description" -> description = parser.nextText()
                    "media:content" -> imageUrl = parser.getAttributeValue(null, "url") ?: ""
                }
                XmlPullParser.END_TAG -> if (parser.name == "item" && title.isNotEmpty()) {
                    articles.add(Article(title, description, link, imageUrl))
                    Log.d("NewsParser", "Added article: $title, Image URL: $imageUrl")
                }
            }
            eventType = parser.next()
        }
        return articles
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}