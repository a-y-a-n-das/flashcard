package com.example.flashcard

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ImageButton
import com.example.flashcard.backup.BackupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: HomeCategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var database: AppDatabase
    private var sessionStartTime: Long = 0
    private var totalTimeSpentToday: Long = 0
    private var sessionStart: Long = 0







    override fun onResume() {
        super.onResume()
        sessionStart = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val sessionEnd = System.currentTimeMillis()
        val duration = (sessionEnd - sessionStart) / 1000  // in seconds

        val minutes = (duration / 60).toInt().coerceAtLeast(1) // avoid 0-minute sessions
        UsageTracker().updateTimeSpentForToday(this, minutesSpent = minutes)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getInstance(this)



        // Inside MainActivity





        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)

        if (isFirstRun) {
            BackupManager.restoreFromFirebase(this, database)

            prefs.edit().putBoolean("is_first_run", false).apply()
        }




        // Call backup
        //BackupManager.backupToFirebase(this, database)

        // Call restore
         //BackupManager.restoreFromFirebase(this, database)
        val syncButton = findViewById<ImageButton>(R.id.Backup)

        syncButton.setOnClickListener {
            val db = AppDatabase.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val hasLocalData = db.cardDao().getAllCards().isNotEmpty() ||
                        db.categoryDao().getAllCategoriesForBackup().isNotEmpty() ||
                        db.countDao().getAllCounts().isNotEmpty()

                withContext(Dispatchers.Main) {
                    if (hasLocalData) {
                        // Backup first, then restore
                        BackupManager.backupToFirebase(this@MainActivity, db) {
                            BackupManager.restoreFromFirebase(this@MainActivity, db)
                        }
                    } else {
                        // Local is empty: restore only
                        BackupManager.restoreFromFirebase(this@MainActivity, db)
                    }
                }
            }
        }




        // Initialize RecyclerView
        categoryRecyclerView = findViewById(R.id.reviewCardList)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = HomeCategoryAdapter(emptyList<Category>().toMutableList())
        categoryRecyclerView.adapter = categoryAdapter


        lifecycleScope.launch(){
            if(database.countDao().getCount() == null)
                database.countDao().insertCount(num=  0, today= LocalDate.now())

        }


        loadReviewCategories()
    }



    private fun loadReviewCategories(){
        lifecycleScope.launch() {
            database.categoryDao().getAllCategories().collect { categories ->
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
