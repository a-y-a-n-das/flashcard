package com.example.flashcard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardList>)

    @Query("DELETE FROM cards WHERE userId = :userId")
    suspend fun clearAll(userId: String)

    @Query("SELECT * FROM cards WHERE userId = :userId")
    suspend fun getAllCards(userId: String): List<CardList>

    @Upsert
    suspend fun upsertCard(card: CardList)

    @Query("SELECT COUNT(*) FROM cards WHERE question = :question AND userId = :userId")
    suspend fun isQuestionExists(question: String, userId: String): Int

    @Delete
    suspend fun deleteCard(card: CardList)

    @Query("SELECT * FROM cards WHERE categoryId = :categoryId AND userId = :userId")
    suspend fun getCardsByCategoryNow(categoryId: Int, userId: String): List<CardList>


    @Query("SELECT * FROM cards WHERE categoryId = :categoryId AND userId = :userId")
    fun getCardsByCategory(categoryId: Int, userId: String): Flow<List<CardList>>

    @Query("SELECT question FROM cards WHERE categoryId = :categoryId AND userId = :userId")
    suspend fun getQuestionsByCategory(categoryId: Int, userId: String): List<String>

    @Query("SELECT answer FROM cards WHERE categoryId = :categoryId AND userId = :userId")
    suspend fun getAnswersByCategory(categoryId: Int, userId: String): List<String>

    @Query("SELECT COUNT(*) FROM cards WHERE categoryId = :categoryId AND userId = :userId AND (" +
            "score = 0 OR " +
            "(score = 1 AND lastReviewDate < :today) OR " +
            "(score = 2 AND lastReviewDate < :yesterday))"
    )
    suspend fun getCardCount(categoryId: Int, today: LocalDate, yesterday: LocalDate, userId: String): Int

    @Query("SELECT question FROM cards " +
            "WHERE categoryId = :categoryId AND userId = :userId AND (" +
            "score = 0 OR " +
            "(score = 1 AND lastReviewDate < :today) OR " +
            "(score = 2 AND lastReviewDate < :yesterday) OR " +
            "(score = 3 AND lastReviewDate < :dayDayBeforeYesterday))"
    )
    suspend fun getReviewCardsQuestionsByCategory(
        categoryId: Int,
        today: LocalDate,
        yesterday: LocalDate,
        dayDayBeforeYesterday: LocalDate,
        userId: String
    ): List<String>

    @Query("SELECT answer FROM cards " +
            "WHERE categoryId = :categoryId AND userId = :userId AND (" +
            "score = 0 OR " +
            "(score = 1 AND lastReviewDate < :today) OR " +
            "(score = 2 AND lastReviewDate < :yesterday) OR " +
            "(score = 3 AND lastReviewDate < :dayDayBeforeYesterday))"
    )
    suspend fun getReviewCardsAnswersByCategory(
        categoryId: Int,
        today: LocalDate,
        yesterday: LocalDate,
        dayDayBeforeYesterday: LocalDate,
        userId: String
    ): List<String>

    @Query("SELECT cardId FROM cards WHERE question = :question AND userId = :userId")
    suspend fun getCardIdByQuestion(question: String, userId: String): Int

    @Query("UPDATE cards " +
            "SET score = CASE " +
            "WHEN score = 2 AND :score = 2 THEN 3 " +
            "ELSE :score " +
            "END, " +
            "lastReviewDate = :lastReviewDate " +
            "WHERE cardId = :cardId AND userId = :userId"
    )
    suspend fun updateCardScoreAndDate(cardId: Int, score: Int, lastReviewDate: LocalDate, userId: String)

    @Query("SELECT COUNT(*) FROM cards WHERE categoryId = :categoryId AND userId = :userId")
    suspend fun getTotalCardCount(categoryId: Int, userId: String): Int

    @Query("""
        SELECT COUNT(*) FROM cards WHERE categoryId = :categoryId AND userId = :userId AND (
            (score = 1 AND lastReviewDate >= :today) OR 
            (score = 2 AND lastReviewDate >= :yesterday)
        )
    """)
    suspend fun getCardReviewedCount(
        categoryId: Int,
        today: LocalDate,
        yesterday: LocalDate,
        userId: String
    ): Int
}