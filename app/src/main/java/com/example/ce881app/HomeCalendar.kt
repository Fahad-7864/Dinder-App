package com.example.ce881app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import android.util.Log

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
 * -Using Gson Type token
 *
 *
 * No modifications have been made to the Gson source code.
 *
 * More information about Gson and its usage can be found at:
 * https://github.com/google/gson
 *
 * The inclusion of Gson follows the licensing terms provided by its maintainers.
 */

/**
 * This class manages the display of recipes in a grid format based on a selected date.
 * Users are directed to this screen from the home page after selecting a specific date.
 * Once on this screen, users can browse through various recipes, which they can potentially add
 * to their chosen date. Each recipe can be clicked to view more detailed information about it.
 * This activity serves as a middle interface.
 */
class HomeCalendar : AppCompatActivity() {
    private lateinit var allRecipes: List<Recipe>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    // Initialises the activity, sets up the RecyclerView with a GridLayoutManager, and handles recipe item clicks.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_calendar)

        recyclerView = findViewById(R.id.recipesRecyclerView)
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        recyclerView.layoutManager = GridLayoutManager(this, (screenWidthDp / 180).toInt())

        allRecipes = loadRecipesFromAssets() ?: listOf() // Load recipes once here
        adapter = RecipeAdapter(allRecipes).apply {
            onItemClick = { recipe ->
                Log.d("Recipe Clicked", "Recipe ID: ${recipe.id}")
                startActivity(
                    Intent(this@HomeCalendar, DetailedRecipeActivity::class.java).apply {
                        putExtra("RECIPE_ID", recipe.id)
                        putExtra("FROM_CALENDAR", true)
                    }
                )
            }
        }
        recyclerView.adapter = adapter
        setupSearchView()
    }


    // Sets up the search view to filter recipes based on user input.
    private fun setupSearchView() {
        val searchView: androidx.appcompat.widget.SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterRecipes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterRecipes(it) }
                return true
            }
        })
    }

    // Filters the list of recipes based on the search query and updates the adapter.
    private fun filterRecipes(query: String) {
        val filteredRecipes = allRecipes.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.ingredients.any { ingredient -> ingredient.contains(query, ignoreCase = true) }
        }
        adapter.updateData(filteredRecipes) // Update adapter with filtered data
    }
    // Loads recipes from a JSON file stored in the assets folder and returns them as a list.

    private fun loadRecipesFromAssets(): List<Recipe>? {
        return try {
            val jsonString = assets.open("recipes.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<RecipesWrapper>() {}.type
            val recipesWrapper = Gson().fromJson<RecipesWrapper>(jsonString, type)
            recipesWrapper.recipes
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
