package com.example.flashcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView


class CategoryAdapter(private val categories: List<String>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Inflate the item layout
       val view = LayoutInflater.from(parent.context).inflate(R.layout.homecard, parent, false)
            return CategoryViewHolder(view)
      }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Set the dynamic text for each item
        val category = categories[position]
        holder.textView.text = category

        // Optionally, change the vector asset based on position
        when (position % 3) {  // Use modulo to repeat 3 designs
            0 -> holder.imageButton.setBackgroundResource(R.drawable.c1) // Replace with your vector asset
            1 -> holder.imageButton.setBackgroundResource(R.drawable.c2) // Replace with your vector asset
            else -> holder.imageButton.setBackgroundResource(R.drawable.c3) // Replace with your vector asset
        }
    }

    override fun getItemCount(): Int = categories.size

    // ViewHolder class to hold references to the views
    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.categoryText)
        val imageButton: ImageButton = view.findViewById(R.id.nextButton)
    }
}
