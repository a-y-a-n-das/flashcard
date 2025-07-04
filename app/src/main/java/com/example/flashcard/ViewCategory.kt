package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ViewCategory : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var cardAdapter: CardAdapter
    private var categoryId: Int = -1
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_category)

        database = AppDatabase.getInstance(this)
        userId = getSharedPreferences("AppData", MODE_PRIVATE).getString("userId", "") ?: ""

        val addCardButton: ImageButton = findViewById(R.id.addView_Card)
        val categoryTitleTextView: TextView = findViewById(R.id.category_Title)
        val backButton: ImageButton = findViewById(R.id.backCategories)

        val categoryName = intent.getStringExtra("Category_name") ?: ""
        categoryTitleTextView.text = "Cards in $categoryName"

        backButton.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.cardViewRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter(listOf()) { card -> deleteCard(card) }
        recyclerView.adapter = cardAdapter

        lifecycleScope.launch {
            // Log all categories to debug user mismatch if needed
            val allCategories = database.categoryDao().getAllCategoriesForBackup()
            allCategories.forEach {
                Log.d("DEBUG", "Category: ${it.categoryName}, userId=${it.userId}, id=${it.categoryId}")
            }

            categoryId = database.categoryDao().getCategoryId(categoryName, userId) ?: -1
            if (categoryId != -1) {
                Log.d("ViewCategory", "Category ID found: $categoryId for name=$categoryName and userId=$userId")
                loadCards()
            } else {
                Log.e("ViewCategory", "Category not found: name=$categoryName, userId=$userId")
                Toast.makeText(
                    this@ViewCategory,
                    "⚠ Category not found. Restore may be incomplete or corrupted.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@ViewCategory, CategoriesActivity::class.java))
                finish()
            }
        }

        addCardButton.setOnClickListener {
            if (categoryId != -1) {
                val intent = Intent(this, addCard::class.java)
                intent.putExtra("categoryId", categoryId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid category ID. Cannot add card.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCards() {
        lifecycleScope.launch {
            val initial = database.cardDao().getCardsByCategoryNow(categoryId, userId)
            cardAdapter.updateData(initial)

            database.cardDao().getCardsByCategory(categoryId, userId).collect { updated ->
                cardAdapter.updateData(updated)
            }
        }
    }

    private fun deleteCard(card: CardList) {
        lifecycleScope.launch {
            database.cardDao().deleteCard(card)
            val updatedCards = database.cardDao().getCardsByCategory(card.categoryId, userId).first()
            cardAdapter.updateData(updatedCards)
            Toast.makeText(this@ViewCategory, "Card deleted", Toast.LENGTH_SHORT).show()
        }
    }
}
