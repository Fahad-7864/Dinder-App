package com.example.ce881app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
 * No modifications have been made to the Gson source code.
 *
 * More information about Gson and its usage can be found at:
 * https://github.com/google/gson
 *
 * The inclusion of Gson follows the licensing terms provided by its maintainers.
 */

/**
 * Utility object for managing shared preferences within the app.
 * This includes functions for storing and retrieving user preferences and recipe data.
 */
object SharedPreferencesUtil {

    private const val PREF_NAME = "AppPreferences"
    private const val DATE_KEY = "recipe_added_to_date_"

    // Stores the last selected date in shared preferences.
    fun storeSelectedDate(context: Context, date: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LastSelectedDate", date).apply()
    }
    // Retrieves the last selected date from shared preferences.
    fun retrieveLastSelectedDate(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("LastSelectedDate", null)
    }

    //  Marks a specific day as selected in shared preferences.
    fun markDayAsSelected(context: Context, date: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("selected_$date", true).apply()
    }
    // Checks if a specific day is marked as selected in shared preferences.
    fun isDaySelected(context: Context, date: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("selected_$date", false)
    }

    //Clears all shared preferences, effectively resetting the app to a default state.
    fun resetAllPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    // Saves the list of liked recipes to shared preferences.
    fun saveLikedRecipes(context: Context, recipes: List<Recipe>) {
        val gson = Gson()
        val json = gson.toJson(recipes.filter { it.isLiked })  // Serialise only liked recipes
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString("LikedRecipes", json)
            .apply()
    }
    // loads the list of likd recipes from shared preferences.
    fun loadLikedRecipes(context: Context): List<Recipe>? {
        val json = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString("LikedRecipes", null)
        return json?.let {
            val type = object : TypeToken<List<Recipe>>() {}.type
            Gson().fromJson<List<Recipe>>(it, type)
        }
    }
    // Stores the last selected date and recipe data in shared preferences.
    fun storeRecipeData(context: Context, date: String, recipeId: Int, recipeTitle: String) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt("LastAddedRecipeId_$date", recipeId)
        editor.putString("LastAddedRecipeTitle_$date", recipeTitle)
        editor.apply()
    }

    // Retrieves the recipe ID and title for a specific date from shared preferences.
    fun getRecipeData(context: Context, date: String): Pair<Int, String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val recipeId = prefs.getInt("LastAddedRecipeId_$date", -1)
        val recipeTitle = prefs.getString("LastAddedRecipeTitle_$date", "No Recipe") ?: "No Recipe"
        return Pair(recipeId, recipeTitle)
    }

}




