package com.example.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class addCard : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var categoryId: Int = 0
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        database = AppDatabase.getInstance(this)

        val question = findViewById<EditText>(R.id.question)
        val answer = findViewById<EditText>(R.id.answer)
        val submit = findViewById<Button>(R.id.addCardButton)
        val addCardsTitle: TextView = findViewById(R.id.addCardsTitle)

        currentUserId = UserSession.getCurrentUserId(this) ?: ""
        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "⚠️ User not signed in!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        categoryId = intent.getIntExtra("categoryId", 0)
        lifecycleScope.launch {
            val category = database.categoryDao().getCategoryName(categoryId)
            addCardsTitle.text = "Add Cards to ${category ?: "Unknown"}"
        }

        submit.setOnClickListener {
            val questionText = question.text.toString().trim()
            val answerText = answer.text.toString().trim()

            if (questionText.isBlank() || answerText.isBlank()) {
                Toast.makeText(this, "❗ Question and Answer can't be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val card = CardList(
                question = questionText,
                answer = answerText,
                categoryId = categoryId,
                userId = currentUserId,
                lastReviewDate = LocalDate.now() // ✅ Correct usage
            )

            lifecycleScope.launch {
                val exists = database.cardDao().isQuestionExists(card.question, currentUserId)
                if (exists > 0) {
                    Toast.makeText(this@addCard, "⚠️ Question already exists!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                database.cardDao().upsertCard(card)
                database.countDao().updateCount(today = LocalDate.now(), num = 1, userId = currentUserId)

                Toast.makeText(this@addCard, "✅ Card added!", Toast.LENGTH_SHORT).show()
                val count = database.countDao().getCount(currentUserId)
                Log.d("CardCount", count.toString())
            }
        }
    }

    fun bHome(v: View) {
        val intent = Intent(this, ViewCategory::class.java)
        lifecycleScope.launch {
            val category = database.categoryDao().getCategoryName(categoryId)
            intent.putExtra("Category_name", category ?: "")
            startActivity(intent)
            finish()
        }
    }
}
