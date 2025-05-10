package com.example.newsappliaction.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappliaction.data.remote.news.ArticlesItem
import com.example.newsappliaction.data.remote.news.Source
import com.example.newsappliaction.databinding.ActivityFavoritesBinding
import com.example.newsappliaction.di.Injection
import com.example.newsappliaction.ui.adapter.NewsAdapter
import com.example.newsappliaction.ui.detail.NewsDetailActivity
import com.example.newsappliaction.ui.viewmodel.NewsViewModel

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Favorite News"

        viewModel = Injection.provideNewsViewModel(this)

        setupRecyclerView()
        setupFab()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(
            onItemClick = { article ->
                val intent = Intent(this, NewsDetailActivity::class.java).apply {
                    putExtra("ARTICLE", article)
                }
                startActivity(intent)
            },
            onFavoriteClick = { article, isFavorite ->
                // In favorites activity, if already favorite (which should be true),
                // then remove from favorites
                viewModel.removeFavorite(article)
            }
        )

        binding.rvFavorites.apply {
            this.adapter = this@FavoritesActivity.adapter
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
        }
    }

    private fun setupFab() {
        binding.fabScrollToTop.setOnClickListener {
            binding.rvFavorites.smoothScrollToPosition(0)
        }

        // Initially hide the FAB
        binding.fabScrollToTop.hide()

        // Show FAB only when scrolled down
        binding.rvFavorites.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Show FAB when not at the top
                if (!recyclerView.canScrollVertically(-1)) {
                    // At the top of the list
                    binding.fabScrollToTop.hide()
                } else {
                    // Scrolled down, show the FAB
                    binding.fabScrollToTop.show()
                }
            }
        })
    }

    private fun observeFavorites() {
        viewModel.getAllFavorites().observe(this) { favorites ->
            val articles = favorites.map { favorite ->
                ArticlesItem(
                    title = favorite.title,
                    url = favorite.url,
                    urlToImage = favorite.urlToImage,
                    publishedAt = favorite.publishedAt,
                    description = favorite.description,
                    source = Source(name = favorite.sourceName)
                )
            }

            adapter.submitList(articles)

            // Manually update the favorite status for each article
            articles.forEach { article ->
                article.url?.let { url ->
                    adapter.updateFavoriteStatus(url, true)
                }
            }

            // Show or hide empty state
            if (articles.isEmpty()) {
                binding.emptyStateCard.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
                binding.fabScrollToTop.hide()
            } else {
                binding.emptyStateCard.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE

                // Show FAB if we're not at the top of the list
                if (!binding.rvFavorites.canScrollVertically(-1)) {
                    binding.fabScrollToTop.hide()
                } else {
                    binding.fabScrollToTop.show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}