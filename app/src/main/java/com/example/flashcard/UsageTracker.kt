package com.example.flashcard

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate

class UsageTracker {



    fun updateTimeSpentForToday(context: Context, minutesSpent: Int) {
        val prefs = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val existingJson = prefs.getString("dailyTimeMap", "{}")

        val mapType = object : TypeToken<MutableMap<String, Int>>() {}.type
        val dailyTimeMap: MutableMap<String, Int> = Gson().fromJson(existingJson, mapType) ?: mutableMapOf()

        dailyTimeMap[today] = (dailyTimeMap[today] ?: 0) + minutesSpent

        val updatedJson = Gson().toJson(dailyTimeMap)
        prefs.edit().putString("dailyTimeMap", updatedJson).apply()
    }

}