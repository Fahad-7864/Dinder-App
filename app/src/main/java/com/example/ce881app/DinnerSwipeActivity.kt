package com.example.ce881app

import android.app.AlertDialog
import android.os.Bundle
import com.yuyakaido.android.cardstackview.CardStackView
import android.view.animation.LinearInterpolator
import android.view.View
import androidx.activity.ComponentActivity
// Import statements from the CardStackView library
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import com.google.gson.Gson
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import com.google.gson.reflect.TypeToken
import java.io.IOException
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * Main activity class that handles the recipe card swiping functionality
 * allowing users to like or dislike recipes presented in a card stack format.
 */
/**
 *  * This activity uses the CardStackView library by Yuyakaido, which is used under the Apache License, Version 2.0.
 *  the full terms of the license are available in the LICENSE.txt file at the root of this project,
 *  * or online at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * This activity uses the CardStackView library developed by Yuyakaido to present a swipeable stack of recipe cards.
 * The library is used to manage the card stack layout, animations, and swipe detection.
 * Specifically, the CardStackLayoutManager, CardStackAdapter, and related classes from the library
 * are integrated for the implementation of the swiping functionality.
 *
 * Reference usage includes:
 * - CardStackView for displaying the card stack view in the layout.
 * - CardStackLayoutManager for managing the layout of the cards.
 * - CardStackListener for handling swipe events on the cards.
 * - is used in activity_dinner_swipe.xml to display the card stack view.
 *
 * The extent of usage of the library is confined to the functionalities provided by the CardStackView
 * to enable the swiping of cards within the DinnerSwipeActivity.
 * No modifications have been made to the library's source code.
 *
 * The library is included as a dependency in the project's build.gradle file and imported directly into this Kotlin file.
 * No other third-party classes or libraries are used in this project.
 *
 * The library's source and additional documentation can be found at:
 * https://github.com/yuyakaido/CardStackView/tree/master?tab=readme-ov-file#installation
 *
 * Usage of this library falls under the guidelines provided by the University's Academic Offences Procedures,
 * ensuring that all third-party code is appropriately referenced.
 */

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

class DinnerSwipeActivity : ComponentActivity(), CardStackListener {

    private lateinit var cardStackView: CardStackView
    private lateinit var adapter: CardStackAdapter
    private lateinit var layoutManager: CardStackLayoutManager
    private val likedRecipes = mutableListOf<Recipe>()
    private val dislikedRecipes = mutableListOf<Recipe>()
    private var navigationMenu: NavigationMenu? = null
    private var likeCount = 0
    private val sessionLikedRecipes = mutableListOf<Recipe>()
    private var alertDialog: AlertDialog? = null

     //The threshold number of likes needed to trigger an event.
    companion object {
        const val LIKES_THRESHOLD = 3
    }

    // Activity's onCreate method where initialisation happens
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dinner_swipe)
        // Initialise NavigationMenu
        navigationMenu = NavigationMenu(this, findViewById<View>(android.R.id.content))

        // Find the overlay FrameLayout and set an OnClickListener to it
        val overlayFrame = findViewById<FrameLayout>(R.id.overlay_frame)
        overlayFrame.setOnClickListener {
            // Hide the overlay when it's tapped
            it.visibility = View.GONE
        }

        // Make the overlay visible at the start
        overlayFrame.visibility = View.VISIBLE

        // Set up the CardStackView and its properties
        cardStackView = findViewById(R.id.card_stack_view)
        layoutManager = CardStackLayoutManager(this, this).apply {
            setStackFrom(StackFrom.None)
            setVisibleCount(3)
            setTranslationInterval(8.0f)
            setScaleInterval(0.95f)
            setSwipeThreshold(0.3f)
            setMaxDegree(20.0f)
            setDirections(Direction.FREEDOM)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
        // Load recipes from assets and update the adapter
        adapter = CardStackAdapter(emptyList())
        cardStackView.layoutManager = layoutManager
        cardStackView.adapter = adapter

        // Load recipes from assets and update the adapter
        val recipes = loadRecipesFromAssets(this)

        // Load liked recipes from SharedPreferences
        val persistedLikedRecipes = SharedPreferencesUtil.loadLikedRecipes(this)
        if (persistedLikedRecipes != null) {
            likedRecipes.addAll(persistedLikedRecipes)  // Merge loaded liked recipes with the current session
        }

        // Setup the menu button to show the dialog
        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        menuButton.setOnClickListener {
            showMenuDialog(this)
        }
        // Update the adapter with the loaded recipes
        adapter.setRecipes(recipes)
        adapter.notifyDataSetChanged()
    }


    // Method to load recipe data from a JSON file stored in assets and filter by category
    private fun loadRecipesFromAssets(context: Context, selectedCategories: List<String> = emptyList()): List<Recipe> {
        val jsonFileString = getJsonDataFromAsset(context, "recipes.json")
        if (jsonFileString == null) {
            Log.e("DinnerSwipeActivity", "Unable to load JSON file.")
            return emptyList()
        } else {
            Log.d("DinnerSwipeActivity", jsonFileString)
        }
        val gson = Gson()
        val wrapperType = object : TypeToken<RecipesWrapper>() {}.type
        val recipesWrapper: RecipesWrapper? = gson.fromJson(jsonFileString, wrapperType)

        // Shuffle and filter recipes based on the selected categories
        val allRecipes = recipesWrapper?.recipes ?: emptyList()
        val filteredAndShuffledRecipes = if (selectedCategories.isEmpty()) {
            allRecipes.shuffled() // Shuffle all recipes if no categories are selected
        } else {
            allRecipes.filter { it.cuisine in selectedCategories }.shuffled() // Filter and then shuffle recipes
        }

        return filteredAndShuffledRecipes
    }

    // Helper method to read JSON data from the assets directory
    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("DinnerSwipeActivity", "Error reading JSON file from assets", ioException)
            ioException.printStackTrace()
            null
        }
    }

    /**
     * Displays a dialog with cuisine category selection options. Users can choose one or more categories
     * from which they want to see recipes. It dynamically creates checkboxes for each available cuisine.
     */
    private fun showMenuDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.menu_dinner_swiper, null)
        val buttonConfirmCategory = dialogView.findViewById<Button>(R.id.buttonConfirmCategory)
        val checkBoxContainer = dialogView.findViewById<LinearLayout>(R.id.checkboxContainer)

        val cuisines = listOf(
            "Greek", "Southern US", "Filipino", "Indian", "Dessert",
            "Spanish", "Italian", "Mexican", "Thai", "Vietnamese",
            "Chinese", "Jamaican", "Korean", "Japanese", "Moroccan",
            "Irish", "French", "British", "Cajun"
        )

        val selectedCategories = mutableListOf<String>()

        // Create checkboxes for each cuisine
        cuisines.forEach { cuisine ->
            val checkBox = CheckBox(context).apply {
                text = cuisine
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategories.add(cuisine)
                    } else {
                        selectedCategories.remove(cuisine)
                    }
                }
            }
            checkBoxContainer.addView(checkBox)
        }
        // Set up the confirm button to filter recipes
        buttonConfirmCategory.setOnClickListener {
            // Handle category confirmation
            if (selectedCategories.isNotEmpty()) {
                // Load recipes for the selected categories
                val filteredRecipes = loadRecipesFromAssets(context, selectedCategories)
                // Update the adapter with the filtered recipes
                adapter.setRecipes(filteredRecipes)
                adapter.notifyDataSetChanged()
            }
            alertDialog?.dismiss()
        }

        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        alertDialog?.show()
    }


    // Methods implementing CardStackListener for handling swipe events
    override fun onCardDragging(direction: Direction, ratio: Float) {}
    override fun onCardSwiped(direction: Direction) {
        val position = layoutManager.topPosition // Get the current top position
        val swipedRecipe = adapter.getRecipeAtPosition(position - 1) // Adjust for 0 indexing
        // Add swiped recipe to liked or disliked list based on the swipe direction
        when (direction) {
            Direction.Right -> {
                swipedRecipe.isLiked = true
                // Check if the recipe is not already in the liked list before adding
                if (!likedRecipes.any { it.id == swipedRecipe.id }) {
                    likedRecipes.add(swipedRecipe)
                    sessionLikedRecipes.add(swipedRecipe)
                    likeCount++ // Increment like counter

                    // Check if exactly three recipes have been liked in this session
                    if (likeCount == LIKES_THRESHOLD) {
                        // Choose a random recipe from the three liked in this session
                        val chosenRecipe = sessionLikedRecipes.random()
                        navigateToRecipeDetail(chosenRecipe.id)

                        // Clear the session liked recipes and reset like count
                        sessionLikedRecipes.clear()
                        likeCount = 0
                    }
                }
            }
            Direction.Left -> {
                swipedRecipe.isLiked = false // Mark as not liked
                dislikedRecipes.add(swipedRecipe)
            }
            else -> {
            // Handle other directions
            }
        }
        SharedPreferencesUtil.saveLikedRecipes(this, likedRecipes)

        // Check if all recipes have been swiped
        if (position == adapter.itemCount) {
        // TO DO
        }
    }

    // Method to navigate to the detailed recipe activity
    private fun navigateToRecipeDetail(recipeId: Int) {
        val intent = Intent(this, DetailedRecipeActivity::class.java).apply {
            putExtra("RECIPE_ID", recipeId)
        }
        startActivity(intent)
    }



    // Other CardStackListener methods
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View, position: Int) {}
    override fun onCardDisappeared(view: View, position: Int) {}
}
