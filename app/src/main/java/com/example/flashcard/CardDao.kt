package com.example.flashcard

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CardDao {

    @Upsert
    suspend fun upsertCard(card: CardList)

    @Query("SELECT COUNT(*) FROM cards WHERE question = :question")
    suspend fun isQuestionExists(question: String): Int

    @Delete
    suspend fun deleteCard(card: CardList)

    @Query("SELECT * FROM cards WHERE categoryId = :categoryId")
    fun getCardsByCategory(categoryId: Int): Flow<List<CardList>>

    @Query("SELECT question FROM cards WHERE categoryId = :categoryId")
    suspend fun getQuestionsByCategory(categoryId: Int): List<String>

    @Query("SELECT answer FROM cards WHERE categoryId = :categoryId")
    suspend fun getAnswersByCategory(categoryId: Int): List<String>

    @Query("SELECT COUNT(*) FROM cards WHERE categoryId = :categoryId AND(" +
                "score = 0 OR " +
                "(score = 1 AND lastReviewDate < :today) OR " +
                "(score = 2 AND lastReviewDate < :yesterday))"
    )
    suspend fun getCardCount(categoryId: Int, today: LocalDate, yesterday: LocalDate): Int

    @Query("SELECT question FROM cards " +
                "WHERE categoryId = :categoryId AND (" +
                "score = 0 OR " +
                "(score = 1 AND lastReviewDate < :today) OR " +
                "(score = 2 AND lastReviewDate < :yesterday))"
    )
    suspend fun getReviewCardsQuestionsByCategory(
        categoryId: Int,
        today: LocalDate,
        yesterday: LocalDate
    ): List<String>

    @Query("SELECT answer FROM cards " +
            "WHERE categoryId = :categoryId AND (" +
            "score = 0 OR " +
            "(score = 1 AND lastReviewDate < :today) OR " +
            "(score = 2 AND lastReviewDate < :yesterday))"
    )
    suspend fun getReviewCardsAnswersByCategory(
        categoryId: Int,
        today: LocalDate,
        yesterday: LocalDate
    ): List<String>

    @Query("SELECT cardId FROM cards WHERE question= :question")
    suspend fun getCardIdByQuestion(question: String): Int

    @Query("UPDATE cards SET score = :score, lastReviewDate = :lastReviewDate WHERE cardId = :cardId")
    suspend fun updateCardScoreAndDate(cardId: Int, score: Int, lastReviewDate: LocalDate)

}