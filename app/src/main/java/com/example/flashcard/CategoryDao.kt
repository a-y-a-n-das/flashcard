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
    suspend fun insertCategories(categories: List<Category>)

    @Query("DELETE FROM categories")
    suspend fun clearAll()

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesForBackup(): List<Category>


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

  //  @Insert
    //suspend fun insertAllCategories(categories: List<Category>)  // Insert a list of categories
}
