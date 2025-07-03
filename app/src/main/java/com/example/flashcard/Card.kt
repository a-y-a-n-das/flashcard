package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDate


class Card : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var badButton: ImageButton
    private lateinit var okButton: ImageButton
    private lateinit var goodButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var database: AppDatabase
    private var cardid: Int =0

    private var currentPosition = 0
    private var flashCards = listOf<String>()
    private var flashCardsSolutions = listOf<String>()
    private var category_name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        database = AppDatabase.getInstance(this)
        category_name = intent.getStringExtra("categoryName").toString()

        // Initialize views
        viewPager = findViewById(R.id.viewPager1)
        badButton = findViewById(R.id.Bad)
        okButton = findViewById(R.id.Ok)
        goodButton = findViewById(R.id.Good)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById<TextView>(R.id.tvCardCounter)

        progressBar.progress = 1

        lifecycleScope.launch {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val dayDayBeforeYesterday = yesterday.minusDays(2)

            try {
                // Fetch categoryId asynchronously
                val categoryId = withContext(Dispatchers.IO) {
                    database.categoryDao().getCategoryId(category_name)?.toInt() ?: 0
                }

                if (categoryId == 0) {
                    Log.e("CardActivity", "Invalid category ID")
                    return@launch
                }

                // Fetch flashcards and solutions concurrently
                val flashCardsDeferred: Deferred<List<String>> = async(Dispatchers.IO) {
                    database.cardDao().getReviewCardsQuestionsByCategory(categoryId, today, yesterday, dayDayBeforeYesterday)
                }

                val flashCardsSolutionsDeferred: Deferred<List<String>> = async(Dispatchers.IO) {
                    database.cardDao().getReviewCardsAnswersByCategory(categoryId, today, yesterday, dayDayBeforeYesterday)
                }

                // Await for both results
                flashCards = flashCardsDeferred.await()
                flashCardsSolutions = flashCardsSolutionsDeferred.await()

                // Check if no cards are available
                if (flashCards.isEmpty() || flashCardsSolutions.isEmpty()) {
                    Log.e("CardActivity", "No flashcards or solutions found")
                    return@launch
                }

                // Update UI after data is fetched
                progressBar.max = flashCards.size
                progressText.text = "${currentPosition + 1}/${flashCards.size}"

                // Set up the adapter with the flashcards
                val adapter = FlashCardAdapter(flashCards) { position ->
                    showSolution(position)
                }
                viewPager.adapter = adapter
                viewPager.isUserInputEnabled = false

            } catch (e: Exception) {
                Log.e("CardActivity", "Error fetching data: ${e.message}")
            }
        }

        // Set up feedback buttons
        badButton.setOnClickListener {
            updateCardScore(0)
            slideToNextCard()
        }

        okButton.setOnClickListener {
            updateCardScore(1)
            slideToNextCard()
        }

        goodButton.setOnClickListener {
            updateCardScore(2)
            slideToNextCard()
        }
    }

    private fun updateCardScore(score: Int) {
        val cardName = flashCards[currentPosition].toString()
        lifecycleScope.launch(){
        cardid = database.cardDao().getCardIdByQuestion(cardName).toString().toInt()
        val today = LocalDate.now()
        // Update the card in the database with the new score and last review date
        database.cardDao().updateCardScoreAndDate(cardid, score, today)}
    }
    public fun backHome(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // This method is triggered when the user swipes to the next card
    private fun slideToNextCard() {
        if(currentPosition == flashCards.size - 1) {
            // End of flashcards
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        currentPosition = (currentPosition + 1) % (viewPager.adapter?.itemCount ?: 1)
        viewPager.setCurrentItem(currentPosition, true)
        progressBar.progress += 1
        val cardCounterText = "${currentPosition + 1}/${flashCards.size}"
        findViewById<TextView>(R.id.tvCardCounter).text = cardCounterText
    }

    // This method is triggered when the ImageButton is clicked
    fun showSolution(position: Int) {
        val solutionText = flashCardsSolutions[position]
        val solutionTextView = findViewById<TextView>(R.id.solText)
        solutionTextView.text = solutionText

        // Toggle visibility of the solution
        solutionTextView.visibility = if (solutionTextView.visibility == View.INVISIBLE) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}
