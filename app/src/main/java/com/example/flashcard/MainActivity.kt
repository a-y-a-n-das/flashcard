package com.example.flashcard

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: HomeCategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var database: AppDatabase
    private var sessionStart: Long = 0

    override fun onResume() {
        super.onResume()
        sessionStart = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val sessionEnd = System.currentTimeMillis()
        val duration = (sessionEnd - sessionStart) / 1000  // seconds
        val minutes = (duration / 60).toInt().coerceAtLeast(1)
        UsageTracker().updateTimeSpentForToday(this, minutesSpent = minutes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getInstance(this)

        val userId = UserSession.getCurrentUserId(this)
        if (userId == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        val currentUserId = userId

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        if (isFirstRun) {
            // Restore only once on first launch
            com.example.flashcard.backup.BackupManager.restoreFromFirebase(this, database, currentUserId)
            prefs.edit().putBoolean("is_first_run", false).apply()
        }

        categoryRecyclerView = findViewById(R.id.reviewCardList)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = HomeCategoryAdapter(mutableListOf())
        categoryRecyclerView.adapter = categoryAdapter

        lifecycleScope.launch {
            if (database.countDao().getCount(currentUserId) == null) {
                database.countDao().insertCount(num = 0, today = LocalDate.now(), userId = currentUserId)
            }
        }

        loadReviewCategories()
    }

    private fun loadReviewCategories() {
        lifecycleScope.launch {
            val userId = UserSession.getCurrentUserId(this@MainActivity) ?: return@launch
            database.categoryDao().getAllCategories(userId).collect { categories ->
                categoryAdapter.categories = categories.toMutableList()
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }

    fun addCard(v: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
    }

    fun gotoStats(v: View) {
        val intent = Intent(this, Stats::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
}
