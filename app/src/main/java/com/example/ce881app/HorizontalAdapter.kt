package com.example.ce881app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



/**
 * Adapter for a RecyclerView that displays a list of recipes horizontally.
 * This adapter is used to populate views with recipe data and handle click events.
 *
 */
class HorizontalAdapter(
    private val recipes: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit) : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {
    /**
     * ViewHolder class defined as an inner class to maintain direct access to the adaptr's
     * private properties such as `recipes` and `onItemClick`.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(recipes[position])
                }
            }
        }
    }

    // Uses layout inflation to create a new view to be managed by the ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.horizontal_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Binds the data at the specified position in the data list to the views in the ViewHolder.
     * This includes setting the text of the TextView to the recipe's title and the image resource
     * of the ImageView to the recipe's image.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.textView.text = recipe.title
        val context = holder.imageView.context
        val resourceId = context.resources.getIdentifier(recipe.imageRes, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.imageView.setImageResource(resourceId)
        } else {
            Log.e("HorizontalAdapter", "Image resource not found for ${recipe.imageRes}")
        }
    }

    // Returns the total number of items in the dataset held by the adapter.
    override fun getItemCount(): Int = recipes.size
}
