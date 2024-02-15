package com.example.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie

class CategoryAdpater(private val categories: List<Category>): RecyclerView.Adapter<CategoryAdpater.CategoryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val movie = categories[position]
        holder.bind(movie)
    }

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(categories: Category){
            val txtTitle: TextView = itemView.findViewById(R.id.text_title)
            txtTitle.text = categories.name

            val rvCategory = itemView.findViewById<RecyclerView>(R.id.rv_category)
            rvCategory.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvCategory.adapter = MovieAdpater(categories.movies)
        }

    }
}