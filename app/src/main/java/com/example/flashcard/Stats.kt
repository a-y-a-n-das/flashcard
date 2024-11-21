package com.example.flashcard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import android.view.View
import android.app.ActivityOptions
import android.content.res.ColorStateList
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat

class Stats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = resources.getColor(R.color.lblue, theme)
        window.navigationBarColor = resources.getColor(R.color.black, theme)
        setContentView(R.layout.activity_stats)
        val home: ImageButton = findViewById(R.id.home2)
        val acc: ImageButton = findViewById(R.id.acc2)
        acc.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.pur, theme))
        home.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.inactive, theme))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    public fun goHome(v: View){
        intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right)
        startActivity(intent, options.toBundle())
    }

}