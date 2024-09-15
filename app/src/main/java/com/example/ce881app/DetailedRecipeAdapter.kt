package com.example.ce881app

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * A class that handles the binding of detailed recipe information to a specific view layout.
 * This includes setting text, images, and dynamically creating views based on the recipe data.
 *
 */
class DetailedRecipeViewHandler(
    private val recipe: Recipe,
    private val rootView: View) {
    // Method to bind recipe details to the respective UI components in the rootView
    fun bindRecipeDetails() {
        // Finding UI components within the rootView by their ID
        val image: ImageView = rootView.findViewById(R.id.image_recipe)
        val title: TextView = rootView.findViewById(R.id.text_recipe_title)
        val prepTime: TextView = rootView.findViewById(R.id.text_prep_time)
        val serves: TextView = rootView.findViewById(R.id.text_serves)
        val vegetarian: ImageView = rootView.findViewById(R.id.image_vegetarian)
        val ingredients: TextView = rootView.findViewById(R.id.text_ingredients)
        // Create a numbered list from the instructions
        val numberedInstructions = recipe.instructions?.mapIndexed { index, step ->
            "${index + 1}. $step"
        }?.joinToString("\n\n") ?: "No instructions available"


        val chiliPepperContainer: LinearLayout = rootView.findViewById(R.id.chili_pepper_container)
        chiliPepperContainer.removeAllViews()  // Clear previous views if any

        // This will add chili pepper icons based on the spice level
        for (i in 0 until recipe.spiceLevel) {
            val chiliImageView = ImageView(rootView.context).apply {
                setImageResource(R.drawable.ic_chili_pepper)
                val size = rootView.context.resources.getDimensionPixelSize(R.dimen.chili_pepper_size)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = rootView.context.resources.getDimensionPixelSize(R.dimen.chili_pepper_margin)
                }
            }
            chiliPepperContainer.addView(chiliImageView)
        }
        // Find the TextView for instructions and set the numbered instructions
        val instructions: TextView = rootView.findViewById(R.id.text_instructions)
        instructions.text = numberedInstructions
        // Setting the text for title, preparation time, and servings from the recipe object
        title.text = recipe.title
        prepTime.text = "Prep Time: ${recipe.prepTime}"
        serves.text = "Serves: ${recipe.serves}"
        // Joining the ingredients list into a single string with bullets and setting it to the ingredients TextView
        ingredients.text = recipe.ingredients?.joinToString("\n") { "• $it" } ?: "No ingredients available"

        // This will set the image resource based on the recipe imageRes
        val imageResId = rootView.context.resources.getIdentifier(
            recipe.imageRes, "drawable", rootView.context.packageName)
        if (imageResId != 0) image.setImageResource(imageResId)

        // This will set the visibility of the vegetarian icon based on the recipe's vegetarian status
        vegetarian.visibility = if (recipe.isVegetarian) View.VISIBLE else View.GONE
    }
}
