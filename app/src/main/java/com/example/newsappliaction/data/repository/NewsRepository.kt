package com.example.newsappliaction.data.repository

import android.content.Context
import android.util.Log
import com.example.newsappliaction.data.local.FavoriteNews
import com.example.newsappliaction.data.local.FavoriteNewsDao
import com.example.newsappliaction.data.local.NewsDatabase
import com.example.newsappliaction.data.remote.news.ArticlesItem
import com.example.newsappliaction.data.remote.news.NewsResponse
import com.example.newsappliaction.data.remote.news.NewsApiService
import com.example.newsappliaction.data.remote.news.NewsConfig

class NewsRepository(private val newsApiService: NewsApiService,
                     private val favoriteNewsDao: FavoriteNewsDao
) {

    /**
     * Fetches everything based on the specified query.
     *
     * @param query The search query (default is "news").
     * @return A Result containing the NewsResponse or an error.
     */
    suspend fun getEverything(query: String = "us"): Result<NewsResponse> {
        return try {
            val response = newsApiService.everything(searchQuery = query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching everything: ${e.message}")
            Result.failure(e)
        }
    }
    /**
     * Fetches top headlines based on the specified category and country.
     *
     * @param category The category of news to fetch.
     * @param country The country code (default is "us").
     * @return A Result containing the NewsResponse or an error.
     */
    suspend fun getNewsByCategory(category: String, country: String = "us"): Result<NewsResponse> {
        return try {
            val response = newsApiService.getTopHeadlines(
                country = country,
                category = category
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching news: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Searches for news articles based on the specified query.
     *
     * @param query The search query.
     * @return A Result containing the NewsResponse or an error.
     */
    suspend fun searchNews(query: String): Result<NewsResponse> {
        return try {
            val response = newsApiService.searchNews(searchQuery = query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error searching news: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Saves a favorite article to the database.
     *
     * @param article The article to save as a favorite.
     */
    suspend fun saveFavorite(article: ArticlesItem) {
        favoriteNewsDao.insertFavorite(
            FavoriteNews(
                url = article.url ?: "",
                title = article.title ?: "",
                description = article.description,
                publishedAt = article.publishedAt,
                urlToImage = article.urlToImage,
                sourceName = article.source?.name
            )
        )
    }

    /**
     * Removes a favorite article from the database.
     *
     * @param article The article to remove from favorites.
     */
    suspend fun removeFavorite(article: ArticlesItem) {
        favoriteNewsDao.deleteFavorite(
            FavoriteNews(
                url = article.url ?: "",
                title = article.title ?: "",
                description = article.description,
                publishedAt = article.publishedAt,
                urlToImage = article.urlToImage,
                sourceName = article.source?.name
            )
        )
    }

    /**
     * Retrieves all favorite articles from the database.
     *
     * @return A list of FavoriteNews objects.
     */
    fun getAllFavorites() = favoriteNewsDao.getAllFavorites()

    /**
     * Checks if a specific article is marked as favorite.
     *
     * @param url The URL of the article to check.
     * @return A LiveData<Boolean> indicating whether the article is a favorite.
     */
    fun isFavorite(url: String) = favoriteNewsDao.isFavorite(url)



    companion object {
        @Deprecated("Use constructor injection instead",
            ReplaceWith("Injection.provideNewsRepository(context)"))
        fun getInstance(context: Context): NewsRepository {
            val database = NewsDatabase.getDatabase(context)
            return NewsRepository(
                NewsConfig.newsApiService,
                database.favoriteNewsDao()
            )
        }
    }
}