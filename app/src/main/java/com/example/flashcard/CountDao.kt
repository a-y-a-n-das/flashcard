package com.example.flashcard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface CountDao {

    @Query("SELECT * FROM count")
    suspend fun getAllCounts(): List<Count>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounts(counts: List<Count>)

    @Query("DELETE FROM count WHERE userId = :userId")
    suspend fun clearAll(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCount(count: Count)

    @Query("SELECT cards FROM count WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getCount(userId: String): Int?

    @Query("UPDATE count SET cards = CASE WHEN date = :today THEN cards + :num ELSE :num END, date = :today WHERE userId = :userId")
    suspend fun updateCount(today: LocalDate, num: Int, userId: String)

    // Room does not officially support INSERT via @Query (not portable), but you can keep it if it works in your version
    @Query("INSERT INTO count (cards, date, userId) VALUES (:num, :today, :userId)")
    suspend fun insertCountRaw(num: Int, today: LocalDate, userId: String)

    // ADD THIS HELPER:
    suspend fun insertCount(num: Int, today: LocalDate, userId: String) {
        insertCount(Count(cards = num, date = today, userId = userId))
    }
}