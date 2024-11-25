package com.example.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import android.widget.TextView



class addCard : AppCompatActivity() {
    lateinit var database: AppDatabase
    var categoryId: Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        database = AppDatabase.getInstance(this)

        val question = findViewById<EditText>(R.id.question)
        val answer = findViewById<EditText>(R.id.answer)
        val submit = findViewById<Button>(R.id.addCardButton)

        val addCardsTitle: TextView = findViewById<TextView>(R.id.addCardsTitle)

        lifecycleScope.launch {
            categoryId = intent.getIntExtra("categoryId", 0)
            val category = database.categoryDao.getCategoryName(categoryId)
            addCardsTitle.text = "Add Cards to $category"
        }

        submit.setOnClickListener(){
            val questionText = question.text.toString()
            val answerText = answer.text.toString()
            if (question == null || answer == null || submit == null) {
                Log.e("Views", "Some views are not initialized.")
            }
            val card = CardList(question = questionText, answer = answerText, categoryId = categoryId)
            lifecycleScope.launch() {
                val exists = database.cardDao.isQuestionExists(card.question)
                if (exists > 0) {
                    Toast.makeText(it.context, "Question already exists!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                database.cardDao.upsertCard(card)
                Toast.makeText(it.context, "Card added!", Toast.LENGTH_SHORT).show()
            }


        }


    }
        public fun bHome(v: View) {
            val intent = Intent(this, ViewCategory::class.java)

            lifecycleScope.launch {
                val category = database.categoryDao.getCategoryName(categoryId)
                intent.putExtra("Category_name", category)
                startActivity(intent)
            }
        }

}