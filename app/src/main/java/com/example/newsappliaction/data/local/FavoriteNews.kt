package com.example.newsappliaction.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_news")
data class FavoriteNews(
    @PrimaryKey
    val url: String,
    val title: String,
    val description: String?,
    val publishedAt: String?,
    val urlToImage: String?,
    val sourceName: String?
)