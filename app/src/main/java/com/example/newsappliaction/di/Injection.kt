package com.example.newsappliaction.di

import android.content.Context
import com.example.newsappliaction.data.local.NewsDatabase
import com.example.newsappliaction.data.remote.news.NewsConfig
import com.example.newsappliaction.data.remote.news.NewsApiService
import com.example.newsappliaction.data.repository.NewsRepository
import com.example.newsappliaction.ui.viewmodel.NewsViewModel

/**
 * Provides dependencies for the application using manual dependency injection.
 * This class centralizes dependency creation to make the code more maintainable
 * and testable.
 */
object Injection {
    /**
     * Provides a NewsRepository instance with proper dependencies.
     */
    fun provideNewsRepository(context: Context): NewsRepository {
        val database = NewsDatabase.getDatabase(context)
        return NewsRepository(
            NewsConfig.newsApiService,
            database.favoriteNewsDao()
        )
    }

    /**
     * Provides a NewsApiService instance.
     */
    private fun provideNewsApiService(): NewsApiService {
        return NewsConfig.newsApiService
    }

    /**
     * Provides a NewsViewModel instance with proper dependencies.
     */
    fun provideNewsViewModel(context: Context): NewsViewModel {
        return NewsViewModel(provideNewsRepository(context))
    }
}