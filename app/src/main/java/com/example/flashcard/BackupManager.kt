package com.example.flashcard.backup

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.flashcard.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

object BackupManager {

    private val storage = Firebase.storage
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            try {
                val dateStr = json?.asString ?: return@JsonDeserializer LocalDate.of(1970, 1, 1)
                if (dateStr == "0000-00-00") {
                    LocalDate.of(1970, 1, 1)
                } else {
                    LocalDate.parse(dateStr)
                }
            } catch (e: Exception) {
                LocalDate.of(1970, 1, 1)
            }
        })
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .create()

    private const val CARDS_FILE = "backups/cards.json"
    private const val CATEGORIES_FILE = "backups/categories.json"
    private const val COUNTS_FILE = "backups/counts.json"

    fun backupToFirebase(context: Context, db: AppDatabase, onSuccess: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cards: List<CardList> = db.cardDao().getAllCards()
                val categories: List<Category> = db.categoryDao().getAllCategoriesForBackup()
                val counts: List<Count> = db.countDao().getAllCounts()
                Log.d("Backup", "Cards: $cards")
                Log.d("Backup", "Categories: $categories")
                Log.d("Backup", "Counts: $counts")

                storage.reference.child(CARDS_FILE)
                    .putBytes(gson.toJson(cards).toByteArray()).await()

                storage.reference.child(CATEGORIES_FILE)
                    .putBytes(gson.toJson(categories).toByteArray()).await()

                storage.reference.child(COUNTS_FILE)
                    .putBytes(gson.toJson(counts).toByteArray()).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ Backup complete", Toast.LENGTH_SHORT).show()
                    onSuccess?.invoke()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun restoreFromFirebase(context: Context, db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cardsJson = storage.reference.child(CARDS_FILE).getBytes(1024 * 1024).await()
                val categoriesJson = storage.reference.child(CATEGORIES_FILE).getBytes(1024 * 1024).await()
                val countsJson = storage.reference.child(COUNTS_FILE).getBytes(1024 * 1024).await()

                val cards: List<CardList> = gson.fromJson(String(cardsJson), object : TypeToken<List<CardList>>() {}.type)
                val categories: List<Category> = gson.fromJson(String(categoriesJson), object : TypeToken<List<Category>>() {}.type)
                val counts: List<Count> = gson.fromJson(String(countsJson), object : TypeToken<List<Count>>() {}.type)

                db.cardDao().clearAll()
                db.categoryDao().clearAll()
                db.countDao().clearAll()

                db.categoryDao().insertCategories(categories)
                db.cardDao().insertCards(cards)
                db.countDao().insertCounts(counts)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ Restore complete", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
