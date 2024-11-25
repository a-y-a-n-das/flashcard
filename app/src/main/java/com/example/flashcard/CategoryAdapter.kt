package com.example.flashcard



import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.MutableList


class CategoryAdapter(private var dataset: MutableList<Category>, private val onDeleteClickListener: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: Button = view.findViewById(R.id.categoryTitle)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.categoryTitle.text = dataset[position].categoryName
        val categoryName = viewHolder.categoryTitle.text.toString()
        viewHolder.categoryTitle.setOnClickListener {
            val intent = Intent(it.context, ViewCategory::class.java)
            intent.putExtra("Category_name", categoryName )
            it.context.startActivity(intent)

        }

        viewHolder.deleteButton.setOnClickListener {
            onDeleteClickListener(dataset[position])
            Toast.makeText(it.context, "Category deleted!", Toast.LENGTH_SHORT).show()
        }

    }

    fun updateData(newCategories: MutableList<Category>) {
        dataset = newCategories
        notifyDataSetChanged()
    }
    override fun getItemCount() = dataset.size

}