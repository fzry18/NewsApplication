package com.example.newsappliaction.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteNewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteNews: FavoriteNews)

    @Delete
    suspend fun deleteFavorite(favoriteNews: FavoriteNews)

    @Query("SELECT * FROM favorite_news ORDER BY publishedAt DESC")
    fun getAllFavorites(): LiveData<List<FavoriteNews>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_news WHERE url = :url)")
    fun isFavorite(url: String): LiveData<Boolean>
}