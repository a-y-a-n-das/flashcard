package com.example.flashcard

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = listOf("Category 1", "Category 2", "Category 3")

        val recyclerView = findViewById<RecyclerView>(R.id.reviewCardList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CategoryAdapter(categories)
        val imageButton:ImageButton = findViewById(R.id.acc)
    }

        public fun next(v: View) {
            val intent = Intent(this, Card::class.java)
            startActivity(intent)
        }

        public fun gotoStats(v: View) {
            val newColor = ContextCompat.getColor(this, R.color.pur)
            val intent = Intent(this, Stats::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
            startActivity(intent, options.toBundle())
        }

        public fun addCard(v: View) {
            val intent = Intent(this, addCard::class.java)
            startActivity(intent)
        }

}

