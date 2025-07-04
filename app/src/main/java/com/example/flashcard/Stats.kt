package com.example.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import android.view.View
import android.app.ActivityOptions
import android.content.res.ColorStateList
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.bumptech.glide.Glide
import com.example.flashcard.backup.BackupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Stats : AppCompatActivity() {
    lateinit var database: AppDatabase
    private lateinit var currentUserId: String
    private val RC_SIGN_IN = 100
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.lblue, theme)
        window.navigationBarColor = resources.getColor(R.color.black, theme)
        setContentView(R.layout.activity_stats)
        database = AppDatabase.getInstance(this)

        val prefs = getSharedPreferences("AppData", MODE_PRIVATE)

        val categoryStatusTextView: TextView = findViewById(R.id.categoryStatusTextView)

        val userId = UserSession.getCurrentUserId(this)
        if (userId == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
        currentUserId = userId.toString()

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategoriesForBackup()
            val categoryStats = mutableListOf<String>()

            val today = LocalDate.now()
            val yesterday = today.minusDays(1)


            for (category in categories) {
                val reviewed = database.cardDao().getCardReviewedCount(category.categoryId, today, yesterday, currentUserId)
                val total = database.cardDao().getTotalCardCount(category.categoryId, currentUserId)
                val notReviewed = total - reviewed

                if (total > 0) {
                    categoryStats.add("${category.categoryName}: $notReviewed cards remaining to reviewed")
                }
            }

            val top4 = categoryStats.take(4)
            categoryStatusTextView.text = top4.joinToString("\n")
        }


        val lastActiveDate = prefs.getString("lastActiveDate", null)
        val today1 = LocalDate.now()
        val yesterday = today1.minusDays(1)
        val streakCount = prefs.getInt("streakCount", 0)

        val lastDate = try {
            LocalDate.parse(lastActiveDate ?: "")
        } catch (e: Exception) {
            Log.e("Stats", "Invalid lastActiveDate: $lastActiveDate", e)
            today1.minusDays(2) // force reset
        }

        val newStreak = when {
            lastDate == yesterday -> streakCount + 1
            lastDate == today1 -> streakCount
            else -> 1
        }


        prefs.edit()
            .putString("lastActiveDate", today1.toString())
            .putInt("streakCount", newStreak)
            .apply()

        findViewById<TextView>(R.id.streakText).text = "🔥 $newStreak"



        val barChart = findViewById<BarChart>(R.id.barChart)
        val json = prefs.getString("dailyTimeMap", "{}")
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        val timeMap: Map<String, Int> = Gson().fromJson(json, mapType) ?: emptyMap()

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        val t = LocalDate.now()
        for (i in 6 downTo 0) {
            val date = t.minusDays(i.toLong())
            val key = date.toString()
            val minutes = timeMap[key] ?: 0
            entries.add(BarEntry((6 - i).toFloat(), minutes.toFloat()))
            labels.add(date.dayOfWeek.name.take(3)) // MON, TUE...
        }

        val dataSet = BarDataSet(entries, "Minutes per Day")
        dataSet.color = ContextCompat.getColor(this, R.color.pur)
        val data = BarData(dataSet)
        data.barWidth = 0.8f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.animateY(1000)

// X-Axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f

// Y-Axis
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f

// Legend
        val legend = barChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.yOffset = 30f
        legend.textSize = 12f
        legend.form = Legend.LegendForm.SQUARE

        barChart.invalidate()



        val strongestText: TextView = findViewById(R.id.strongestText)
        val weakestText: TextView = findViewById(R.id.weakestText)

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategoriesForBackup()
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            val reviewCounts = mutableMapOf<String, Int>()

            for (category in categories) {
                val reviewCount = database.cardDao().getCardCount(category.categoryId, today, yesterday, currentUserId)
                reviewCounts[category.categoryName] = reviewCount
            }

            val weakest = reviewCounts.maxByOrNull { it.value }
            val strongest = reviewCounts.minByOrNull { it.value }

            strongestText.text = "${strongest?.key ?: "\n Add more cards to see stats"}"
            weakestText.text = "${weakest?.key ?: "\n Add more cards to see stats"}"
        }







        val home: ImageButton = findViewById(R.id.home2)
        val acc: ImageButton = findViewById(R.id.acc2)
        acc.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.pur, theme))
        home.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.inactive, theme))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val profileImageButton = findViewById<ImageButton>(R.id.profileImageButton)
        val profileNameText = findViewById<TextView>(R.id.profileNameText)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.photoUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .circleCrop()
                .into(profileImageButton)
        }



        val syncButton = findViewById<ImageButton>(R.id.Backup)

        syncButton.setOnClickListener {
            val db = AppDatabase.getInstance(this)
            val userId = UserSession.getCurrentUserId(this)

            if (userId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasLocalData = db.cardDao().getAllCards(userId).isNotEmpty() ||
                            db.categoryDao().getAllCategoriesForBackup().isNotEmpty() ||
                            db.countDao().getAllCounts().isNotEmpty()

                    withContext(Dispatchers.Main) {
                        if (hasLocalData) {
                            BackupManager.backupToFirebase(this@Stats, db, userId) {
                                BackupManager.restoreFromFirebase(this@Stats, db, userId)
                            }
                        } else {
                            BackupManager.restoreFromFirebase(this@Stats, db, userId)
                        }
                    }
                }
            }
        }



        profileNameText.text = account?.displayName ?: account?.email ?: "Account Name"

        // On click: FULL sign out and app reset
        profileImageButton.setOnClickListener {

            val userId = UserSession.getCurrentUserId(this)
            if (userId != null) {
                val db = AppDatabase.getInstance(this)

                // Do the backup first
                lifecycleScope.launch {
                    BackupManager.backupToFirebase(this@Stats, db, userId) {
                        // After backup is complete, then switch account
                        switchAccount()
                    }
                }
            } else {
                // If userId is null, just switch account
                switchAccount()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        // Load all user data (so all UI is correct)
        refreshAllUserData()
    }

    // FULL sign out, clear app data, and restart sign-in flow
    fun switchAccount() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener {
            // 1. Clear SharedPreferences
            getSharedPreferences("AppData", MODE_PRIVATE).edit().clear().apply()

            // 2. Clear user data from Room database
            lifecycleScope.launch {
                database.cardDao().clearAll(currentUserId)
                database.categoryDao().clearAll(currentUserId)
                database.countDao().clearAll(currentUserId)


                // 3. Start sign-in activity as a new task, clearing the back stack
                val intent = Intent(this@Stats, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }


    // ... keep your refreshAllUserData and the rest of your methods as before ...
    private fun refreshAllUserData() {
        // ... unchanged, only loads data for the current user ...
        val prefs = getSharedPreferences("AppData", MODE_PRIVATE)
        val json2 = prefs.getString("dailyTimeMap", "{}")
        val mapType2 = object : TypeToken<Map<String, Int>>() {}.type
        val dailyMap: Map<String, Int> = Gson().fromJson(json2, mapType2)

        val today = LocalDate.now().toString()
        val todayTimeSpent = dailyMap[today] ?: 0
        findViewById<TextView>(R.id.timeView).text = "$todayTimeSpent+"

        // ... rest of your user data refresh logic ...
        // (no sign-out or clear logic here!)
    }

    fun goHome(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }

    fun addCardStats(v: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
    }
}