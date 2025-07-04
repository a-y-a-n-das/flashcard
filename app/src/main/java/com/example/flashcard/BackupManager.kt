package com.example.flashcard.backup

import android.content.Context
import android.widget.Toast
import com.example.flashcard.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

object BackupManager {
    private val gson: Gson = GsonBuilder().create()

    fun backupToFirebase(
        context: Context,
        db: AppDatabase,
        userId: String,
        onSuccess: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cards: List<CardList> = db.cardDao().getAllCards(userId)
                val categories: List<Category> = db.categoryDao().getAllCategoriesForBackup()
                val counts: List<Count> = db.countDao().getAllCounts()
                val storage = Firebase.storage

                val base = "backups/$userId"
                storage.reference.child("$base/cards.json")
                    .putBytes(gson.toJson(cards).toByteArray()).await()
                storage.reference.child("$base/categories.json")
                    .putBytes(gson.toJson(categories).toByteArray()).await()
                storage.reference.child("$base/counts.json")
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

    fun restoreFromFirebase(
        context: Context,
        db: AppDatabase,
        userId: String,
        onSuccess: (() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val base = "backups/$userId"
                val storage = Firebase.storage

                val cardsJson = storage.reference.child("$base/cards.json").getBytes(1024 * 1024).await()
                val categoriesJson = storage.reference.child("$base/categories.json").getBytes(1024 * 1024).await()
                val countsJson = storage.reference.child("$base/counts.json").getBytes(1024 * 1024).await()

                val cardsFromBackup: List<CardList> = gson.fromJson(String(cardsJson), object : TypeToken<List<CardList>>() {}.type)
                val categories: List<Category> = gson.fromJson(String(categoriesJson), object : TypeToken<List<Category>>() {}.type)
                val counts: List<Count> = gson.fromJson(String(countsJson), object : TypeToken<List<Count>>() {}.type)

                val updatedCards = cardsFromBackup.map {
                    val safeDate = try {
                        if (it.lastReviewDate.year <= 0 || it.lastReviewDate.monthValue <= 0) {
                            LocalDate.now()
                        } else {
                            it.lastReviewDate
                        }
                    } catch (e: Exception) {
                        LocalDate.now()
                    }

                    it.copy(userId = userId, lastReviewDate = safeDate)
                }

                db.cardDao().clearAll(userId)
                db.categoryDao().clearAll(userId)
                db.countDao().clearAll(userId)

                val updatedCategories = categories.map {
                    it.copy(userId = userId)
                }
                db.categoryDao().insertCategories(updatedCategories)
                db.cardDao().insertCards(updatedCards)
                db.countDao().insertCounts(counts)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ Restore complete", Toast.LENGTH_SHORT).show()
                    onSuccess?.invoke()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
