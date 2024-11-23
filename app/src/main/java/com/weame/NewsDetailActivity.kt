package com.weame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.weame.databinding.ActivityNewsDetailBinding


class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            url?.let { loadUrl(it) }
        }
    }
}
