package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class Card : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var badButton: ImageButton
    private lateinit var okButton: ImageButton
    private lateinit var goodButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var  progressText : TextView

    private var currentPosition = 0

    private val flashCards = listOf("Card 1", "Card 2", "Card 3") // Example flashcards
    private val flashCardsSolutions = listOf(
        "Solution for Card 1",
        "Solution for Card 2",
        "Solution for Card 3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)


        // Initialize views
        viewPager = findViewById(R.id.viewPager1)
        badButton = findViewById(R.id.Bad)
        okButton = findViewById(R.id.Ok)
        goodButton = findViewById(R.id.Good)
        progressBar = findViewById(R.id.progressBar)
        progressText= findViewById<TextView>(R.id.tvCardCounter)

        progressBar.progress = 1
        progressBar.max = flashCards.size
        progressText.text = "${currentPosition +1}/${flashCards.size}"

        // Set up the adapter and pass the showSolution callback
        val adapter = FlashCardAdapter(flashCards) { position ->
            showSolution(position)
        }
        viewPager.adapter = adapter

        // Disable swipe gestures by intercepting touch events
        viewPager.isUserInputEnabled = false

        // Set up feedback buttons
        badButton.setOnClickListener {
            slideToNextCard()
           }

        okButton.setOnClickListener {
            slideToNextCard()
        }

        goodButton.setOnClickListener {
            slideToNextCard()
        }
    }

    public fun backHome(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    // This method is triggered when the user swipes to the next card
    private fun slideToNextCard() {
        currentPosition = (currentPosition + 1) % (viewPager.adapter?.itemCount ?: 1)
        viewPager.setCurrentItem(currentPosition, true)
        progressBar.progress += 1
        val cardCounterText = "${currentPosition + 1}/${flashCards.size}"
        findViewById<TextView>(R.id.tvCardCounter).text = cardCounterText
    }

    // This method is triggered when the ImageButton is clicked
    fun showSolution(position: Int) {
            // Get the solution for the current card
            val solutionText = flashCardsSolutions[position]

        // Find the TextView in the current layout where you want to show the solution
        val solutionTextView = findViewById<TextView>(R.id.solText)
        solutionTextView.text = solutionText


        // Make the TextView visible to show the solution
        if(solutionTextView.visibility == View.INVISIBLE)
            solutionTextView.visibility = View.VISIBLE
        else
            solutionTextView.visibility = View.INVISIBLE

    }
}
