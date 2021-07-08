package com.volvadvit.memoriesmap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.volvadvit.memoriesmap.R
import com.volvadvit.memoriesmap.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val web = binding.webView
        val dataFromExtra = intent.getStringExtra("web") ?: ""

        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient(){}
        web.loadUrl("https://www.google.com/search?q=${dataFromExtra}")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this@WebViewActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}