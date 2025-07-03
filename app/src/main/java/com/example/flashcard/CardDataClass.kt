package com.example.flashcard

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(tableName = "categories")  // Optional: specify the table name explicitly
data class Category(
    val categoryName: String = "",
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0  // `categoryId` is the primary key, auto-generated
)

@Entity(tableName = "cards")
@TypeConverters(Converters::class)// Optional: specify the table name explicitly
data class CardList(
    val categoryId: Int ,  // Foreign key to the `categories` table
    val question: String = "",
    val answer: String = "",
    @PrimaryKey(autoGenerate = true)
    val cardId: Int = 0,
    var score : Int =0,
    var lastReviewDate: LocalDate = LocalDate.now()
)

@Entity(tableName = "count")
@TypeConverters(Converters::class)// Optional: specify the table name explicitly
data class Count(
    var cards: Int = 0,
    var date: LocalDate = LocalDate.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)