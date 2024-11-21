package com.example.flashcard

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import com.example.flashcard.Category




class CategoriesActivity : AppCompatActivity() {

    private lateinit var categoryDao: CategoryDao
    private lateinit var database: AppDatabase
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Initialize Room database and DAO
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "flashcards-db"
        ).build()
        categoryDao = database.categoryDao()

        // Initialize RecyclerView
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categories)
        categoryRecyclerView.adapter = categoryAdapter

        // Load existing categories from the database
        loadCategories()

        // Set up the "Add Category" button
        val addButton: Button = findViewById(R.id.createCategoryButton) // Button to add a new category
        addButton.setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.categoryEditText).text.toString()
            if (categoryName.isNotEmpty()) {
                // Add the new category to the database
                addCategory(categoryName)
            }
        }
    }

    // Add a new category to the database
    private fun addCategory(categoryName: String) {
        val newCategory = Category(name = categoryName)

        // Run database operations in the background thread
        CoroutineScope(Dispatchers.IO).launch {
            categoryDao.insertCategory(newCategory)

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                categories.add(newCategory)  // Add the new category to the list
                categoryAdapter.notifyItemInserted(categories.size - 1) // Notify RecyclerView of new item
            }
        }
    }

    // Load categories from Room database
    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val categoriesFromDb = categoryDao.getAllCategories()
            withContext(Dispatchers.Main) {
                categories.clear()
                categories.addAll(categoriesFromDb)
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }
}
