package com.example.ce881app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
/**
 * This file uses the Gson library to parse JSON data into Kotlin objects.
 * Gson is a third-party library and its usage here is strictly for the purpose
 * of parsing JSON data as provided by the local assets of the application.
 *
 * Gson is not included in the Kotlin API or Android SDK by default and was
 * added as a dependency in the project's build.gradle file.
 *
 * The use of Gson in this project is limited to the following functionalities:
 * - Converting JSON strings to Kotlin objects.
 * - Converting Kotlin objects to JSON strings.
 *  -Using Gson Type token
 *
 * No modifications have been made to the Gson source code.
 *
 * More information about Gson and its usage can be found at:
 * https://github.com/google/gson
 *
 * The inclusion of Gson follows the licensing terms provided by its maintainers.
 */

/**
 * This adapter manages the display of days in a RecyclerView within a calendar on the homepage.
 * Each day can be clicked to either show options for an existing recipe or to add a new one if none exists.
 * It also changes the background color of a day's view to indicate that a recipe has been added to that date.
 */
class DaysAdapter(
    private val daysList: List<DayInfo>,
    private val onClick: (DayInfo) -> Unit,
    private val isDaySelected: (String) -> Boolean

) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {
    // Provides a reference to the views for each data item.

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayDateTextView: TextView = view.findViewById(R.id.dayDateTextView)
        val dayNameTextView: TextView = view.findViewById(R.id.dayNameTextView)

        // Binds the day information to the view and sets click listeners that determine the interaction based on day selection.
        fun bind(dayInfo: DayInfo, onClick: (DayInfo) -> Unit, isDaySelected: (String) -> Boolean) {
            itemView.setOnClickListener {
                if (isDaySelected(dayInfo.date) && hasRecipe(dayInfo.date, itemView.context)) {
                    showMenuDialog(itemView.context, dayInfo.date)
                } else {
                    onClick(dayInfo)
                }
            }
            dayDateTextView.text = dayInfo.date
            dayNameTextView.text = dayInfo.dayName
        }
        // Checks if there is a recipe associated with the given date.
        private fun hasRecipe(date: String, context: Context): Boolean {
            val prefs = context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
            val recipeId = prefs.getInt("LastAddedRecipeId_$date", -1)
            return recipeId != -1  // Only return true if a valid recipe ID exists
        }


        // Displays a dialog with options for the selected date.
        private fun showMenuDialog(context: Context, date: String) {
            val prefs = context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
            val recipeId = prefs.getInt("LastAddedRecipeId_$date", -1) // Default to -1 if not found

            // Load recipes from assets
            val recipes = loadRecipesFromAssets(context) ?: listOf()
            val recipe = recipes.find { it.id == recipeId }

            val builder = AlertDialog.Builder(context)
            builder.setTitle(if (recipe != null) "Options for $date: ${recipe.title}" else "Options for $date")

            // Dynamic array of choices based on whether a recipe ID exists
            val choices = if (recipe != null) {
                arrayOf("View ${recipe.title}" )
            } else {
                // blank for now
                arrayOf()
            }

            // Set the actions for the dialog items
            builder.setItems(choices) { dialog, which ->
                when (which) {
                    0 -> if (recipe != null) {
                        // Handle showing details of the recipe or editing
                        Intent(context, DetailedRecipeActivity::class.java).also { intent ->
                            intent.putExtra("RECIPE_ID", recipe.id)
                            intent.putExtra("RECIPE_TITLE", recipe.title)  // Pass the title along with the intent
                            context.startActivity(intent)
                        }
                    } else {
                        // Handle stuff
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()
        }


        /**
         * Loads recipes from a JSON file stored in the assets folder
         */
        private fun loadRecipesFromAssets(context: Context): List<Recipe>? {
            return try {
                val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<RecipesWrapper>() {}.type
                val recipesWrapper = Gson().fromJson<RecipesWrapper>(jsonString, type)
                Log.d("LoadRecipes", "Number of recipes loaded: ${recipesWrapper.recipes.size}")
                recipesWrapper.recipes
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
    // Updates the display to show changes when a recipe is added or changed for a day.
    fun updateDaySelection(date: String) {
        val index = daysList.indexOfFirst { it.date == date }
        if (index != -1) {
            notifyItemChanged(index)
        }
    }


    /**
     * Creates new ViewHolder instances for each item in the RecyclerView. This method is called by the
     * RecyclerView when it needs a new ViewHolder to display an item. In this case, the ViewHolder is
     * constructed by inflating the day_item layout, which represents each day in the calendar.
     */    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
        return DayViewHolder(view)
    }

    /**
     *  This method populates the contents of the ViewHolder's itemView with the element
     * from the dataset at the given position. Here, it binds the day information to each view, sets up click
     * listeners, and modifies the background color to indicate whether a recipe has been added on that date.
     *
     */
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayInfo = daysList[position]
        holder.bind(dayInfo, onClick, isDaySelected)

        // Get SharedPreferences to check if a recipe has been added for the day
        val prefs = holder.itemView.context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
        val recipeId = prefs.getInt("LastAddedRecipeId_${dayInfo.date}", -1)

        if (recipeId != -1) {
            holder.itemView.setBackgroundColor(Color.GREEN)
            Log.d("DaysAdapter", "Date colored green: ${dayInfo.date}, Recipe ID: $recipeId")
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            Log.d("DaysAdapter", "Date not colored green: ${dayInfo.date}")
        }
    }



    //Returns the size of your dataset
    override fun getItemCount(): Int = daysList.size
}

// Simple data class to represent day information such as date and name.
data class DayInfo(val date: String, val dayName: String)
