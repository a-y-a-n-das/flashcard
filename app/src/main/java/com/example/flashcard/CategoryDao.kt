package com.example.flashcard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)  // Make sure categories already have userId set

    @Upsert
    suspend fun upsertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // ? Only return categories for the current user
    @Query("SELECT * FROM categories WHERE userId = :userId")
    fun getAllCategories(userId: String): Flow<List<Category>>

    // ? For backup (all, including userId for each)
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesForBackup(): List<Category>

    @Query("DELETE FROM categories WHERE userId = :userId")
    suspend fun clearAll(userId: String)

    // ? Ensure matching user
    @Query("SELECT categoryId FROM categories WHERE categoryName = :categoryName AND userId = :userId")
    suspend fun getCategoryId(categoryName: String, userId: String): Int?

    @Query("SELECT categoryName FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryName(categoryId: Int): String?
}
