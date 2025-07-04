package com.example.flashcard

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate {
        return try {
            dateString?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
        } catch (e: Exception) {
            android.util.Log.e("Converters", "Invalid date string: $dateString", e)
            LocalDate.now() // fallback to current date
        }
    }

}
