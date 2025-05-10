package com.example.newsappliaction

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsappliaction.databinding.ActivityMainBinding
import com.example.newsappliaction.di.Injection
import com.example.newsappliaction.ui.adapter.NewsAdapter
import com.example.newsappliaction.ui.detail.NewsDetailActivity
import com.example.newsappliaction.ui.favorite.FavoritesActivity
import com.example.newsappliaction.ui.viewmodel.NewsViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var toggle: ActionBarDrawerToggle

    private var currentCategory: String? = null

    // Categories for the news
    private val categories = listOf(
        "business", "entertainment", "general",
        "health", "science", "sports", "technology"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupNavigationDrawer()
        setupViewModel()
        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh()
        setupFab()

        // Call observeViewModel here - this was missing!
        observeViewModel()

        // Load initial news with everything endpoint
        loadInitialNews()
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupViewModel() {
        viewModel = Injection.provideNewsViewModel(this)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(
            onItemClick = { article ->
                // Navigate to detail
                val intent = Intent(this, NewsDetailActivity::class.java).apply {
                    putExtra("ARTICLE", article)
                }
                startActivity(intent)
            },
            onFavoriteClick = { article, isFavorite ->
                if (isFavorite) {
                    viewModel.saveFavorite(article)
                } else {
                    viewModel.removeFavorite(article)
                }
            }
        )

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            // Initial setup to show only search icon when collapsed
            setIconifiedByDefault(true)
            isIconified = true
            queryHint = "Search news..."

            // Make the search view background stand out a bit more
            setBackgroundResource(R.drawable.search_background)

            // Fix the search icon color to match the new palette
            val searchIcon = findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchIcon?.setColorFilter(ContextCompat.getColor(context, R.color.colorText))

            // Make entire search view clickable
            setOnClickListener {
                isIconified = false  // Expand when clicked anywhere on the view
                requestFocus()
            }

            // Improve text appearance
            val searchText = findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchText?.apply {
                setTextColor(ContextCompat.getColor(context, R.color.colorText))
                setHintTextColor(ContextCompat.getColor(context, R.color.colorHint))
                textSize = 16f
            }

            // Fix close button color
            val closeButton = findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setColorFilter(ContextCompat.getColor(context, R.color.colorText))
        }

        // Handle search queries
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchNews(it)
                        currentCategory = null
                    }
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Handle close button click
        val closeButton = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener {
            // Clear the query
            binding.searchView.setQuery("", false)

            // Collapse the search view
            binding.searchView.isIconified = true
            binding.searchView.clearFocus()

            // Reload appropriate content
            if (currentCategory != null) {
                viewModel.getNewsByCategory(currentCategory!!)
            } else {
                loadInitialNews()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (currentCategory != null) {
                viewModel.getNewsByCategory(currentCategory!!)
            } else {
                loadAllCategories()
            }
        }
    }

    private fun setupFab() {
        binding.fabFilter.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all -> {
                loadInitialNews()  // Use initial news instead of all categories
                currentCategory = null
            }
            R.id.nav_business -> {
                viewModel.getNewsByCategory("business")
                currentCategory = "business"
            }
            R.id.nav_entertainment -> {
                viewModel.getNewsByCategory("entertainment")
                currentCategory = "entertainment"
            }
            R.id.nav_general -> {
                viewModel.getNewsByCategory("general")
                currentCategory = "general"
            }
            R.id.nav_health -> {
                viewModel.getNewsByCategory("health")
                currentCategory = "health"
            }
            R.id.nav_science -> {
                viewModel.getNewsByCategory("science")
                currentCategory = "science"
            }
            R.id.nav_sports -> {
                viewModel.getNewsByCategory("sports")
                currentCategory = "sports"
            }
            R.id.nav_technology -> {
                viewModel.getNewsByCategory("technology")
                currentCategory = "technology"
            }
            R.id.nav_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
            }
            R.id.nav_settings -> {
                // TODO: Implement settings screen
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        updateFavoriteStatuses()
    }

    private fun updateFavoriteStatuses() {
        val currentList = newsAdapter.currentList
        currentList.forEach { article ->
            article.url?.let { url ->
                viewModel.isFavorite(url).observe(this) { isFavorite ->
                    newsAdapter.updateFavoriteStatus(url, isFavorite)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.newsArticles.observe(this) { articles ->
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.GONE

            if (articles.isEmpty()) {
                binding.tvError.text = "No news found"
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
                newsAdapter.submitList(articles)

                // Update favorite status for all articles
                articles.forEach { article ->
                    article.url?.let { url ->
                        viewModel.isFavorite(url).observe(this) { isFavorite ->
                            newsAdapter.updateFavoriteStatus(url, isFavorite)
                        }
                    }
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (!binding.swipeRefreshLayout.isRefreshing) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            binding.swipeRefreshLayout.isRefreshing = false

            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    private fun loadInitialNews() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getEverything() // Use the new method
    }

    private fun loadAllCategories() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getAllNews(categories)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}