package com.example.ce881app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView



/**
 * Activity that displays recipes liked by the user, with the ability to filter by cuisine categories.
 * It supports navigation to detailed views of each recipe and manages preferences related to liked recipes.
 */
class LikedPageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private var navigationMenu: NavigationMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_page)
        navigationMenu = NavigationMenu(this, findViewById<View>(android.R.id.content))

        recyclerView = findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, calculateNumberOfColumns())
        setupCategoriesMenu()

        setupRecyclerView()
       //resetLikedRecipes()


    }

    /**
     * Configures the RecyclerView with liked recipes. Distinct recipes are loaded from SharedPreferences
     * and displayed.
     */
    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, calculateNumberOfColumns())

        val likedRecipes = SharedPreferencesUtil.loadLikedRecipes(this)?.distinctBy { it.id } ?: listOf()
        adapter = RecipeAdapter(likedRecipes).apply {
            onItemClick = { recipe ->
                navigateToDetailedRecipe(recipe.id)
            }
        }
        recyclerView.adapter = adapter
    }


    // Calculates the number of colums for the grid layout based on screen size and desired item width.
    private fun calculateNumberOfColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 180).toInt() // Assuming each item is 180dp wide
    }


    // Method made for development purposes
    private fun resetLikedRecipes() {
        // Reset liked recipes in shared preferences
        SharedPreferencesUtil.resetAllPreferences(this)
        // Clear current list in adapter and notify to refresh the RecyclerView
        adapter = RecipeAdapter(listOf()) // Create a new adapter with an empty list
        recyclerView.adapter = adapter
    }

    /**
     * Sets up the menu for filtering recipes by categories. Each category button when clicked
     * filters the displayed recipes accordingly.
     */
    private fun setupCategoriesMenu() {
        val categories = arrayOf("All Cuisines","Greek", "Southern US", "Filipino", "Indian", "Dessert",
            "Spanish", "Italian", "Mexican", "Thai", "Vietnamese",
            "Chinese", "Jamaican", "Korean", "Japanese", "Moroccan",
            "Irish", "French", "British", "Cajun") // Add "All Cuisines" at the end

        val container = findViewById<LinearLayout>(R.id.categoriesContainer)
        container.removeAllViews()  // Clear existing views if re-creating the menu

        categories.forEach { category ->
            val button = Button(this).apply {
                text = category
                setOnClickListener {
                    if (category == "All Cuisines") {
                        showAllCuisines()
                    } else {
                        filterRecipesByCategory(category)
                    }
                }
            }
            container.addView(button)
        }
    }
    // Navigates to the DetailedRecipeActivity with the selected recipe.
    private fun navigateToDetailedRecipe(recipeId: Int) {
        val intent = Intent(this, DetailedRecipeActivity::class.java).apply {
            putExtra("RECIPE_ID", recipeId)
        }
        startActivity(intent)
    }
     // Displays all liked recipes without any category filter.
    private fun showAllCuisines() {
        // Load all liked recipes without filtering by category
        val likedRecipes = SharedPreferencesUtil.loadLikedRecipes(this)?.distinctBy { it.id } ?: listOf()
        adapter = RecipeAdapter(likedRecipes)
        recyclerView.adapter = adapter
    }

    //  Filters the liked recipes by the selected category and updates the RecyclerView to display them.
    private fun filterRecipesByCategory(category: String) {
        // Filter recipes based on the selected category
        val filteredRecipes = SharedPreferencesUtil.loadLikedRecipes(this)?.filter {
            it.cuisine == category
        }?.distinctBy { it.id } ?: listOf()
        adapter = RecipeAdapter(filteredRecipes)
        recyclerView.adapter = adapter
    }



}
