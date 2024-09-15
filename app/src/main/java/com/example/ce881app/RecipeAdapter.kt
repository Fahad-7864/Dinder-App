package com.example.ce881app
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



/**
 * Used for HomeCalendar
 * Adapter for displaying recipes in a grid layout in the CalendarWhatsForDinner activity.
 * This adapter manages recipe data and binds it.
 */
class RecipeAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // To handle click events on recipe items
    var onItemClick: ((Recipe) -> Unit)? = null

    // Inflates the recipe grid item layout and returns a new holder instance.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_grid_item, parent, false)
        return RecipeViewHolder(view)
    }

    //Binds each recipe to a view holder. Sets up the click listener for each item.
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recipe)
        }
    }
    // Updates the data set with new recipes and notifies the adapter.
    fun updateData(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }
    // Returns the total number of items in the dataset held by the adapter.
    override fun getItemCount(): Int = recipes.size


     // This class is responsible for setting the recipe details in each item's view.
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_recipe)
        private val titleTextView: TextView = view.findViewById(R.id.text_recipe_title)
        private val prepTimeTextView: TextView = view.findViewById(R.id.text_prep_time)
        private val servesTextView: TextView = view.findViewById(R.id.text_serves)

         /**
          * Binds a recipe object to the views within the ViewHolder.
          * Sets the image resource, title, preparation time, and serving size from the recipe data.
          */
        fun bind(recipe: Recipe) {
            imageView.setImageResource(itemView.context.resources.getIdentifier(recipe.imageRes, "drawable", itemView.context.packageName))
            titleTextView.text = recipe.title
            prepTimeTextView.text = "Prep time: ${recipe.prepTime}"
            servesTextView.text = "Serves: ${recipe.serves}"
        }
    }
}

