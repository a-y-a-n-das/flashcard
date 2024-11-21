package com.example.flashcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashCardAdapter(
    private val flashCards: List<String>,
    private val showSolution: (Int) -> Unit // Callback function
) : RecyclerView.Adapter<FlashCardAdapter.FlashCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_item, parent, false)
        return FlashCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashCardViewHolder, position: Int) {
        holder.bind(flashCards[position])

        // Set up the onClick listener for the arrow button
        holder.arrowButton.setOnClickListener {
            // Toggle solution visibility and arrow image
            if (holder.solutionTextView.visibility == View.INVISIBLE) {
                holder.arrowButton.setImageResource(R.drawable.up)
            } else {
                holder.arrowButton.setImageResource(R.drawable.down)
            }

            // Trigger the showSolution function if needed
            showSolution(position)
        }
    }

    override fun getItemCount(): Int = flashCards.size

    class FlashCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val arrowButton: ImageButton = itemView.findViewById(R.id.showSolution)
        val solutionTextView: TextView = itemView.findViewById(R.id.solText)

        fun bind(card: String) {
            itemView.findViewById<TextView>(R.id.cardText).text = card
        }
    }
}
