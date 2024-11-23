package com.weame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.weame.databinding.ActivityMainBinding
import com.weame.adapter.NewsAdapter
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

        // Set the general news item as selected in navigation drawer
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
                val articles = mutableListOf<Article>()

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (parser.name) {
                                "item" -> {
                                    title = ""
                                    link = ""
                                    description = ""
                                    imageUrl = ""
                                }
                                "title" -> title = parser.nextText()
                                "link" -> link = parser.nextText()
                                "description" -> description = parser.nextText()
                                "media:content" -> {
                                    imageUrl = parser.getAttributeValue(null, "url") ?: ""
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (parser.name == "item" && title.isNotEmpty()) {
                                articles.add(Article(
                                    title = title,
                                    description = description,
                                    url = shortenNewsUrl(link),
                                    urlToImage = imageUrl
                                ))
                                android.util.Log.d("NewsParser", "Added article: $title, Image URL: $imageUrl")
                            }
                        }
                    }
                    eventType = parser.next()
                }

                launch(Dispatchers.Main) {
                    android.util.Log.d("NewsParser", "Total articles: ${articles.size}")
                    newsAdapter.updateNews(articles)
                }

            } catch (e: Exception) {
                android.util.Log.e("NewsParser", "Error fetching news", e)
            }
        }
    }



    private fun shortenNewsUrl(url: String): String {
        return url.split("&url=").lastOrNull()?.split("&")?.firstOrNull() ?: url
    }


    private fun extractImageUrl(description: String): String {
        val imgPattern = "<img[^>]+src=\"([^\"]+)\"".toRegex()
        val matchResult = imgPattern.find(description)?.groupValues?.get(1)

        if (!matchResult.isNullOrEmpty()) {
            return matchResult.split("?").firstOrNull() ?: ""
        }

        return "https://via.placeholder.com/150"
    }




    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
