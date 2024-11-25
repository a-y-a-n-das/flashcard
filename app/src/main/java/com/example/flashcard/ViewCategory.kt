package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlin.system.exitProcess
import android.widget.Toast
import kotlinx.coroutines.flow.first


class ViewCategory : AppCompatActivity() {
    lateinit var database: AppDatabase
    lateinit var cardAdapter: CardAdapter
    private lateinit var cards:List<CardList>
    private var category_id: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_category)
        database = AppDatabase.getInstance(this)


        val addCardButton: ImageButton = findViewById(R.id.addView_Card)
        val category_name = intent.getStringExtra("Category_name").toString()
        val categoryName: TextView = findViewById<EditText>(R.id.category_Title)
        val backCategories: ImageButton = findViewById<ImageButton>(R.id.backCategories)

        categoryName.text = "Cards in $category_name"
        backCategories.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }

        loadCards()

        lifecycleScope.launch() {
            category_id = database.categoryDao.getCategoryId(category_name) ?: 0
            if (category_id != null) {
            category_id = category_id
               Log.e("ViewCategory", "Category ID found for category name: $category_name & $category_id")
                loadCards()
        } else {
            // Handle the case when categoryId is null (e.g., show a message to the user)
            Log.e("ViewCategory", "Category ID not found for category name: $category_name")
                exitProcess(1)
        }
        }

        // Set up RecyclerView
        val cardRecyclerView = findViewById<RecyclerView>(R.id.cardViewRecyclerView)
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter(listOf<CardList>()){card ->
            deleteCard(card)
        }
        cardRecyclerView.adapter = cardAdapter

        addCardButton.setOnClickListener() {
            val intent = Intent(it.context, addCard::class.java)
            intent.putExtra("categoryId", category_id)
            it.context.startActivity(intent)
        }



    }

    private fun loadCards(){
        lifecycleScope.launch {
            // Collect categories using Flow
            Log.d("ViewCategory", "Category ID: $category_id")
            database.cardDao.getCardsByCategory(category_id).collect { cards ->
                Log.d("ViewCategory", "Fetched Cards: $cards")
                cardAdapter.updateData(cards)
            }
        }
    }

    private fun deleteCard(card: CardList){
        lifecycleScope.launch {
            // Delete card from the database
            database.cardDao.deleteCard(card)
            // Update the adapter's list
            val updatedCards = database.cardDao.getCardsByCategory(card.categoryId).first()
            cardAdapter.updateData(updatedCards.toMutableList())

            Toast.makeText(this@ViewCategory, "Card deleted", Toast.LENGTH_SHORT).show()
        }
    }
}