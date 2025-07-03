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

    @Query("DELETE FROM count")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCount(count: Count)

    @Query("SELECT cards FROM count WHERE id = 1")
    suspend fun getCount(): Int?

    @Query("UPDATE count SET cards = CASE WHEN date = :today THEN cards + :num ELSE :num END, date = :today WHERE id = 1")
    suspend fun updateCount(today: LocalDate, num: Int)

    // Rename this one internally to avoid conflict
    @Query("INSERT INTO count (cards, date) VALUES (:num, :today)")
    suspend fun insertCountRaw(num: Int, today: LocalDate)

    // You can optionally wrap it here if needed:
    suspend fun insertCount(num: Int, today: LocalDate, useRaw: Boolean = true) {
        if (useRaw) {
            insertCountRaw(num, today)
        } else {
            insertCount(Count(num, today))
        }
    }
}
