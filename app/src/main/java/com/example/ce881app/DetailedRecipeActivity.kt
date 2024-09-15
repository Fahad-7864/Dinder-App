package com.example.ce881app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
 *
 * No modifications have been made to the Gson source code.
 *
 * More information about Gson and its usage can be found at:
 * https://github.com/google/gson
 *
 * The inclusion of Gson follows the licensing terms provided by its maintainers.
 */

/**
 * Activity for displaying detailed information about a selected recipe. It also allows
 * for the recipe to be added to a specific date if this activity was triggered from
 * the CalendarWhatsForDinner activity.
 */
class DetailedRecipeActivity : AppCompatActivity() {
    private lateinit var recipes: List<Recipe>
    private var navigationMenu: NavigationMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_recipe_activity)
        navigationMenu = NavigationMenu(this, findViewById<View>(android.R.id.content))

        enableEdgeToEdge()

        // Load recipes using the dedicated method
        recipes = loadRecipesFromAssets()

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        val recipe = recipes.find { it.id == recipeId } // Direct access since recipes are already loaded
        val container = findViewById<LinearLayout>(R.id.detailed_recipe_container)
        val detailedView = LayoutInflater.from(this).inflate(R.layout.layout_detailed_recipe_card, container, false)
        val fromCalendar = intent.getBooleanExtra("FROM_CALENDAR", false)

        // Find the button
        val addToCalendarButton = findViewById<Button>(R.id.addToCalendarButton)

        // Set visibility based on whether the activity was started from CalendarWhatsForDinner
        addToCalendarButton.visibility = if (fromCalendar) View.VISIBLE else View.GONE

        val selectedDate = SharedPreferencesUtil.retrieveLastSelectedDate(this)

        // Setup the button click listener to show a toast with the selected date aNd the recipe ID
        findViewById<Button>(R.id.addToCalendarButton).setOnClickListener {
            selectedDate?.let { date ->
                Toast.makeText(this, "Selected date: $date, Recipe ID: $recipeId", Toast.LENGTH_LONG).show()
                SharedPreferencesUtil.markDayAsSelected(this, date)

                // logging the recipe ID
                //Log.d("DetailedRecipeActivity", "Recipe added to calendar: ID $recipeId")
                // Store the recipe ID for the selected date
                val editor = getSharedPreferences("RecipePrefs", MODE_PRIVATE).edit()
                editor.putInt("LastAddedRecipeId_$date", recipeId) // Use date as part of the key to store multiple recipes
                editor.apply()
                // Send broadcast to indicate recipe has been added
                val intent = Intent("com.example.ce881app.DATE_UPDATED")
                intent.putExtra("updated_date", date)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                refreshDaysAdapter(date)

            } ?: Toast.makeText(this, "No date selected", Toast.LENGTH_LONG).show()
        }


        // Setup the button click listener
        recipe?.let {
            val viewHandler = DetailedRecipeViewHandler(it, detailedView)
            viewHandler.bindRecipeDetails()
            container.addView(detailedView)
        } ?: Log.e("DetailedRecipeActivity", "Recipe with ID $recipeId not found")
    }


    private fun refreshDaysAdapter(updatedDate: String) {
        // have found alternative way to do this

    }

    // Method to load recipe data from a JSON file stored in assets
    private fun loadRecipesFromAssets(): List<Recipe> {
        val jsonFileString = getJsonDataFromAsset("recipes.json")
        if (jsonFileString == null) {
            Log.e("DetailedRecipeActivity", "Unable to load JSON file.")
            return emptyList()
        }
        val gson = Gson()
        val type = object : TypeToken<RecipesWrapper>() {}.type
        val recipesWrapper: RecipesWrapper? = gson.fromJson(jsonFileString, type)
        return recipesWrapper?.recipes ?: emptyList()
    }

    // Helper method to read JSON data from the assets directory
    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            applicationContext.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("DetailedRecipeActivity", "Error reading JSON file from assets", ioException)
            ioException.printStackTrace()
            null
        }
    }
}
