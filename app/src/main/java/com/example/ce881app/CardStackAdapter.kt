package com.example.ce881app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter class to display recipe cards in a swipable card stack format.
 * This adapter manages the data set of recipes and binds them to the view holder.
 */
class CardStackAdapter(

    // List of recipes to be displayed in the card stack
    private var recipes: List<Recipe>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {


    // ViewHolder class that holds references to the UI components within each card
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_recipe)
        val title: TextView = view.findViewById(R.id.text_recipe_title)
        val prepTime: TextView = view.findViewById(R.id.text_prep_time)
        val chiliPepperContainer: LinearLayout = view.findViewById(R.id.chili_pepper_container)
        val serves: TextView = view.findViewById(R.id.text_serves)
        val vegetarian: ImageView = view.findViewById(R.id.image_vegetarian)

    }

    // Method to create and return a ViewHolder for a recipe card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recipe_card, parent, false)
        return ViewHolder(view)
    }

    // Method to bind data to the ViewHolder at a specific position in the list of recipes
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.image.setImageResource(
            holder.itemView.context.resources.getIdentifier(
                recipe.imageRes, "drawable", holder.itemView.context.packageName
            )
        )
        holder.title.text = recipe.title
        holder.prepTime.text = "Prep time: ${recipe.prepTime}"
        holder.serves.text = "Serves: ${recipe.serves}"

        // Clear any existing chili peppers
        holder.chiliPepperContainer.removeAllViews()

        // Automatically add chili peppers based on the spice level
        for (i in 0 until recipe.spiceLevel) {
            val chiliImageView = ImageView(holder.chiliPepperContainer.context).apply {
                setImageResource(R.drawable.ic_chili_pepper)
                val size = context.resources.getDimensionPixelSize(R.dimen.chili_pepper_size)
                val margin = context.resources.getDimensionPixelSize(R.dimen.chili_pepper_margin)
                layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                    setMargins(0, 0, margin, 0) // Apply margin to the right side of each chili
                }
            }
            holder.chiliPepperContainer.addView(chiliImageView)
        }


        // Set visibility of vegetarian icon
        holder.vegetarian.visibility = if (recipe.isVegetarian) View.VISIBLE else View.GONE

    }

    // Helper method to get a recipe at a specific position
    fun getRecipeAtPosition(position: Int): Recipe = recipes[position]

    // Returns the total count of items in the adapter
    override fun getItemCount(): Int = recipes.size


    // Method to update the list of recipes and refresh the UI
    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
    }
}
