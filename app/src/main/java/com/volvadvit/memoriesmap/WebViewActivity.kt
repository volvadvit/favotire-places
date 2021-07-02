package com.volvadvit.memoriesmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import com.volvadvit.memoriesmap.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val web = binding.webView
        val dataFromExtra = intent.getStringExtra("web") ?: ""

        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient(){}
        Toast.makeText(this, dataFromExtra, Toast.LENGTH_SHORT).show()
        web.loadUrl("https://www.google.com/search?q=${dataFromExtra}")

    }
}