package com.example.flashcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.MutableList

class CardAdapter(private var dataset: List<CardList>, private val onDeleteClickListener: (CardList) -> Unit): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardTitle: TextView = view.findViewById(R.id.cardTitle)
        val cardDeteleButton: ImageButton = view.findViewById(R.id.deleteCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewcategory_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.cardTitle.text = dataset[position].question.toString()
        viewHolder.cardDeteleButton.setOnClickListener {
            val card = dataset[position]
            onDeleteClickListener(card)
        }


    }

    fun updateData(newCards: List<CardList>) {
        dataset = newCards
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataset.size


}