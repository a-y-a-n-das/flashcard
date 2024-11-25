package com.example.flashcard

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.example.flashcard.HomeCategoryAdapter
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: HomeCategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getInstance(this)

        // Initialize RecyclerView
        categoryRecyclerView = findViewById(R.id.reviewCardList)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = HomeCategoryAdapter(emptyList<Category>().toMutableList())
        categoryRecyclerView.adapter = categoryAdapter


        loadReviewCategories()
    }


    private fun loadReviewCategories(){
        // Load the categories from the database
        lifecycleScope.launch() {
            database.categoryDao.getAllCategories().collect { categories ->
                categoryAdapter.categories = categories as MutableList<Category>
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }



    public fun gotoStats(v: View) {
        val newColor = ContextCompat.getColor(this, R.color.pur)
        val intent = Intent(this, Stats::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }

    public fun addCard(v: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
    }
}
