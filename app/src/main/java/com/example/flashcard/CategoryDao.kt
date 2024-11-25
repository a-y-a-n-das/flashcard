package com.example.flashcard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert
    suspend fun upsertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>  // Changed to return Flow<List<Category>>

    @Query("SELECT categoryId FROM categories WHERE categoryName = :categoryName")
    suspend fun getCategoryId(categoryName: String): Int?

    @Query("SELECT categoryName FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryName(categoryId: Int): String?

    @Insert
    suspend fun insertAllCategories(categories: List<Category>)  // Insert a list of categories
}
