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

class Stats : AppCompatActivity() {
    lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.lblue, theme)
        window.navigationBarColor = resources.getColor(R.color.black, theme)
        setContentView(R.layout.activity_stats)
        val home: ImageButton = findViewById(R.id.home2)
        val acc: ImageButton = findViewById(R.id.acc2)
        acc.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.pur, theme))
        home.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.inactive, theme))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("AppData", MODE_PRIVATE)
        val json2 = prefs.getString("dailyTimeMap", "{}")
        val mapType2 = object : TypeToken<Map<String, Int>>() {}.type
        val dailyMap: Map<String, Int> = Gson().fromJson(json2, mapType2)

        val today = LocalDate.now().toString()
        val todayTimeSpent = dailyMap[today] ?: 0
        findViewById<TextView>(R.id.timeView).text = "$todayTimeSpent+"


        val categoryStatusTextView: TextView = findViewById(R.id.categoryStatusTextView)  // Create this in your XML
        database = AppDatabase.getInstance(this)

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategoriesForBackup()
            val categoryStats = mutableListOf<String>()

            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            for (category in categories) {
                val reviewed = database.cardDao().getCardReviewedCount(category.categoryId, today, yesterday)
                val total = database.cardDao().getTotalCardCount(category.categoryId)
                val notReviewed = total - reviewed

                if (total > 0) {
                    categoryStats.add("${category.categoryName}: $notReviewed / $total to review")
                }
            }


            // Sort by total cards and take top 4
            val top4 = categoryStats.take(4)

            // Join with newline for display
            findViewById<TextView>(R.id.categoryStatusTextView).text = top4.joinToString("\n")
        }




        //streak
        val lastActiveDate = prefs.getString("lastActiveDate", null)
        val streakCount = prefs.getInt("streakCount", 0)

        val today1 = LocalDate.now()
        val yesterday = today1.minusDays(1)

        if (lastActiveDate == null) {
            prefs.edit().putString("lastActiveDate", today.toString())
                .putInt("streakCount", 1).apply()
        } else {
            val lastDate = LocalDate.parse(lastActiveDate)
            val newStreak = when {
                lastDate == yesterday -> streakCount + 1
                lastDate == today1 -> streakCount
                else -> 1 // reset
            }
            prefs.edit().putString("lastActiveDate", today.toString())
                .putInt("streakCount", newStreak).apply()
        }

        // 🔸 Then display it in a TextView
        val finalStreak = prefs.getInt("streakCount", 0)
        findViewById<TextView>(R.id.streakText).text = "🔥 $finalStreak"


        //CHART
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
            labels.add(date.dayOfWeek.name.take(3))  // e.g. "MON", "TUE"
        }

        // Setup dataset
        val dataSet = BarDataSet(entries, "Minutes per Day")
        dataSet.color = ContextCompat.getColor(this, R.color.pur)
        val data = BarData(dataSet)
        data.barWidth = 0.8f

        val legend = barChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false) // Push it outside the chart
        legend.yOffset = 30f         // Add vertical space from chart
        legend.xOffset = 0f          // Optional horizontal spacing
        legend.textSize = 12f
        legend.form = Legend.LegendForm.SQUARE



        // Configure chart
        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.legend.textSize = 12f
        barChart.animateY(1000)

        // X-axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f

        // Y-axis
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f

        barChart.invalidate()




        database = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            // Get the count from the database, with a fallback to 0 if null
            val count = database.countDao().getCount()

            // Log the count value to verify it's correct
            Log.e("count", count.toString())

            // Set the count value to the TextView
            findViewById<TextView>(R.id.cardAdded).text = count.toString() + "+"
        }


        // --- Strongest and Weakest Subject Calculation ---
        val strongestText: TextView = findViewById(R.id.strongestText)
        val weakestText: TextView = findViewById(R.id.weakestText)

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategoriesForBackup()
            val today = java.time.LocalDate.now()
            val yesterday = today.minusDays(1)

            val reviewCounts = mutableMapOf<String, Int>()

            for (category in categories) {
                val reviewCount =
                    database.cardDao().getCardCount(category.categoryId, today, yesterday)
                reviewCounts[category.categoryName] = reviewCount
            }

            val weakest = reviewCounts.maxByOrNull { it.value }
            val strongest = reviewCounts.minByOrNull { it.value }

            strongestText.text = "Strongest: ${strongest?.key ?: "\n Add more cards to see stats"}"
            weakestText.text = "Weakest: ${weakest?.key ?: "\n Add more cards to see stats"}"
        }
    }

    fun goHome(v: View){
        intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }

    fun addCardStats(v: View) {
        val intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
    }

}