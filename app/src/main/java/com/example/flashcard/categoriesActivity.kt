package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var userId: String  // ✅ userId at class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        database = AppDatabase.getInstance(this)

        // ✅ Retrieve current userId from SharedPreferences
        userId = getSharedPreferences("AppData", MODE_PRIVATE).getString("userId", "") ?: ""

        val categoryRecyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        val backToHome: ImageButton = findViewById(R.id.backToHome)
        val categoryNameText = findViewById<EditText>(R.id.categoryNameText)
        val submitCategoryName = findViewById<Button>(R.id.submitCategoryName)

        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        backToHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Initialize adapter with empty list
        categoryAdapter = CategoryAdapter(mutableListOf()) { category ->
            deleteCategory(category)
        }
        categoryRecyclerView.adapter = categoryAdapter

        submitCategoryName.setOnClickListener {
            val name = categoryNameText.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, "Category name cannot be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // ✅ Include userId in category
                val category = Category(categoryName = name, userId = userId)
                database.categoryDao().upsertCategory(category)
                loadCategories()
            }
        }

        loadCategories()
    }

    // ✅ Pass userId here
    private fun loadCategories() {
        lifecycleScope.launch {
            database.categoryDao().getAllCategories(userId).collect { categories ->
                categoryAdapter.updateData(categories.toMutableList())
            }
        }
    }

    private fun deleteCategory(category: Category) {
        lifecycleScope.launch {
            database.categoryDao().deleteCategory(category)
            loadCategories()
        }
    }
}
