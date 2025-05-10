package com.example.newsappliaction.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newsappliaction.R
import com.example.newsappliaction.data.remote.news.ArticlesItem
import com.example.newsappliaction.databinding.ActivityNewsDetailBinding
import com.example.newsappliaction.di.Injection
import com.example.newsappliaction.ui.viewmodel.NewsViewModel

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding
    private lateinit var viewModel: NewsViewModel
    private var currentArticle: ArticlesItem? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = Injection.provideNewsViewModel(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentArticle = intent.getParcelableExtra("ARTICLE")
        currentArticle?.let { displayArticle(it) }
    }

    private fun displayArticle(article: ArticlesItem) {
        binding.apply {
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvContent.text = article.content
            tvSource.text = article.source?.name
            tvPublishedAt.text = article.publishedAt
            tvAuthor.text = article.author ?: "Unknown"

            Glide.with(this@NewsDetailActivity)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivNewsImage)

            // Setup favorite button with proper state tracking
            // Query current favorite state when loading detail
            viewModel.isFavorite(article.url ?: "").observe(this@NewsDetailActivity) { isFav ->
                isFavorite = isFav
                updateFavoriteButtonState()
            }

            // Move the click listener outside the observer to prevent multiple registrations
            btnFavorite.setOnClickListener {
                if (isFavorite) {
                    viewModel.removeFavorite(article)
                } else {
                    viewModel.saveFavorite(article)
                }
                isFavorite = !isFavorite
                updateFavoriteButtonState()
            }

            // Setup read more button
            btnReadMore.setOnClickListener {
                // Open article in WebView or browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(intent)
            }
        }
    }

    private fun updateFavoriteButtonState() {
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}