package com.example.flashcard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeCategoryAdapter(var categories: MutableList<Category>) : RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryViewHolder>() {
    private lateinit var database: AppDatabase
    private val adapterScope = CoroutineScope(Dispatchers.Main)

    class HomeCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.categoryText)
        val imageButton: ImageButton = view.findViewById(R.id.startReview)
        var headingText: TextView = view.findViewById(R.id.headingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        // Inflate the item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.homecard, parent, false)
        return HomeCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {
        // Set the dynamic text for each item
        val category = categories[position]
        holder.textView.text = category.categoryName
        val category_Name = category.categoryName

        // Correct way to get Context for GoogleSignIn
        val context = holder.itemView.context
        val account = com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount(context)
        val currentUserId = account?.id ?: ""

        adapterScope.launch {
            database = AppDatabase.getInstance(context)
            val userId = context.getSharedPreferences("AppData", android.content.Context.MODE_PRIVATE)
                .getString("userId", "") ?: ""
            val id = database.categoryDao().getCategoryId(category_Name, userId)?.toInt() ?: 0
            val count = withContext(Dispatchers.IO) {
                val today = LocalDate.now()
                val yesterday = today.minusDays(1)
                database.cardDao().getCardCount(id, today, yesterday, currentUserId)
            }
            if (count == 0) {
                // Postpone removal to avoid IndexOutOfBoundsException
                withContext(Dispatchers.Main) {
                    if (holder.adapterPosition != RecyclerView.NO_POSITION && holder.adapterPosition < categories.size) {
                        categories = categories.toMutableList().apply {
                            removeAt(holder.adapterPosition)
                        }
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, categories.size)
                    }
                }
            } else {
                // Update the heading text for valid categories
                withContext(Dispatchers.Main) {
                    holder.headingText.text = "Review $count flashcards"
                }
            }
        }

        // Optionally, change the vector asset based on position
        when (position % 3) {  // Use modulo to repeat 3 designs
            0 -> holder.imageButton.setBackgroundResource(R.drawable.c1)
            1 -> holder.imageButton.setBackgroundResource(R.drawable.c2)
            else -> holder.imageButton.setBackgroundResource(R.drawable.c3)
        }

        holder.imageButton.setOnClickListener {
            // Start the review activity
            val intent = Intent(it.context, Card::class.java)
            intent.putExtra("categoryName", category.categoryName)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = categories.size
}