package com.example.newsappliaction.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappliaction.data.remote.news.ArticlesItem
import com.example.newsappliaction.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _newsArticles = MutableLiveData<List<ArticlesItem>>()
    val newsArticles: LiveData<List<ArticlesItem>> = _newsArticles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error



    fun getEverything(query: String = "us") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getEverything(query).fold(
                    onSuccess = { response ->
                        // Set default category for all articles from "everything" endpoint
                        val articles = response.articles?.filterNotNull() ?: emptyList()
                        articles.forEach { article ->
                            article.category = "general"  // Set a default category
                        }
                        _newsArticles.value = articles
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Unknown error"
                        Log.e("NewsViewModel", "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                Log.e("NewsViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNewsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getNewsByCategory(category).fold(
                    onSuccess = { response ->
                        val articles = response.articles?.filterNotNull() ?: emptyList()
                        // Set the category for each article
                        articles.forEach { article ->
                            article.category = category  // Set the correct category
                        }
                        _newsArticles.value = articles
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Unknown error"
                        Log.e("NewsViewModel", "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                Log.e("NewsViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchNews(query).fold(
                    onSuccess = { response ->
                        val articles = response.articles?.filterNotNull() ?: emptyList()
                        // Set default category for search results
                        articles.forEach { article ->
                            article.category = "search"  // Mark as search results
                        }
                        _newsArticles.value = articles
                        _error.value = null
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Unknown error"
                        Log.e("NewsViewModel", "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                Log.e("NewsViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this function to NewsViewModel.kt
    fun getAllNews(categories: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allArticles = mutableListOf<ArticlesItem>()

                // Create a list to track errors
                val errors = mutableListOf<String>()

                // Load news from each category
                categories.forEach { category ->
                    repository.getNewsByCategory(category).fold(
                        onSuccess = { response ->
                            val articles = response.articles?.filterNotNull() ?: emptyList()
                            // Add category info to each article
                            articles.forEach { article ->
                                article.category = category
                            }
                            allArticles.addAll(articles)
                        },
                        onFailure = { exception ->
                            errors.add("${category.capitalize()}: ${exception.message}")
                        }
                    )
                }

                if (allArticles.isNotEmpty()) {
                    // Sort by published date (newest first)
                    allArticles.sortByDescending { it.publishedAt }
                    _newsArticles.value = allArticles
                    _error.value = null
                } else if (errors.isNotEmpty()) {
                    _error.value = errors.joinToString("\n")
                } else {
                    _error.value = "No news available"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                Log.e("NewsViewModel", "Error loading all news: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveFavorite(article: ArticlesItem) {
        viewModelScope.launch {
            repository.saveFavorite(article)
        }
    }

    fun removeFavorite(article: ArticlesItem) {
        viewModelScope.launch {
            repository.removeFavorite(article)
        }
    }

    fun getAllFavorites() = repository.getAllFavorites()

    fun isFavorite(url: String) = repository.isFavorite(url)

}