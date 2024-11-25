package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlin.system.exitProcess

class CategoriesActivity : AppCompatActivity() {
    lateinit var database: AppDatabase
    lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categories:List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        database = AppDatabase.getInstance(this)

        // Set up RecyclerView
        val categoryRecyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        val backToHome : ImageButton = findViewById<ImageButton>(R.id.backToHome)

        val categoryNameText = findViewById<EditText>(R.id.categoryNameText)
        val submitCategoryName = findViewById<Button>(R.id.submitCategoryName)

        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        backToHome.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // Initialize adapter with an empty list
        categoryAdapter = CategoryAdapter(mutableListOf()){ category ->
            deleteCategory(category)
        }
        categoryRecyclerView.adapter = categoryAdapter

        submitCategoryName.setOnClickListener {
            lifecycleScope.launch() {
                val categoryName = categoryNameText.text.toString()
                val category = Category(categoryName = categoryName)
                if(categoryName.isBlank()) {
                    Toast.makeText(it.context, "Category name cannot be blank!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                database.categoryDao.upsertCategory(category)
                loadCategories()
            }
        }

        // Load categories from the database
        loadCategories()
    }


    // Function to load categories from the database
    private fun loadCategories() {
        lifecycleScope.launch {
            // Collect categories using Flow
            database.categoryDao.getAllCategories().collect { categories ->
                categoryAdapter.updateData(categories as MutableList<Category>)  // Update RecyclerView with new data
            }
        }
    }

    private fun deleteCategory(category: Category) {
        lifecycleScope.launch {
            database.categoryDao.deleteCategory(category) // Delete from database

            loadCategories() // Reload categories
        }
    }
    }
