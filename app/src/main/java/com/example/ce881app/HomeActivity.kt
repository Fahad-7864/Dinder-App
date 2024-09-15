package com.example.ce881app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
 * this class displays and interact with a calendar view, showing meals for selected days
 * and managing different recipe categories through horizontal RecyclerViews.
 */

class HomeActivity : AppCompatActivity() {
    private lateinit var daysRecyclerView: RecyclerView
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var daysList: List<DayInfo>
    private var navigationMenu: NavigationMenu? = null
    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var horizontalRecyclerView2: RecyclerView
    private lateinit var horizontalRecyclerView3: RecyclerView
    private lateinit var sectionTitleTextView: TextView
    private lateinit var sectionTitleTextView2: TextView
    private lateinit var sectionTitleTextView3: TextView

     //Initialises components and sets up RecyclerViews on activity creation.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Initialise NavigationMenu
        navigationMenu = NavigationMenu(this, findViewById<View>(android.R.id.content))
        // Initialise the daysList here
        daysList = generateWeekDays()

        // Initialise the RecyclerView property
        daysRecyclerView = findViewById(R.id.daysRecyclerView)

        // Initialise the adapter with daysList
        daysAdapter = DaysAdapter(daysList, { dayInfo ->
            // Handling what happens when a day is clicked
            val selectedDate = dayInfo.date
            SharedPreferencesUtil.storeSelectedDate(this@HomeActivity, selectedDate)
            SharedPreferencesUtil.markDayAsSelected(this@HomeActivity, selectedDate)

            // Intent to start another activity
            val intent = Intent(this, HomeCalendar::class.java)
            intent.putExtra("SELECTED_DATE", dayInfo.date)
            startActivity(intent)
        }, { date ->
            // Check if the day is selected
            SharedPreferencesUtil.isDaySelected(this@HomeActivity, date)
        })

        daysRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        daysRecyclerView.adapter = daysAdapter


        // Initialise section title TextViews
        sectionTitleTextView = findViewById(R.id.sectionTitleTextView)
        sectionTitleTextView2 = findViewById(R.id.sectionTitleTextView2)
        sectionTitleTextView3 = findViewById(R.id.sectionTitleTextView3)

        // Initialise RecyclerViews
        horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView)
        horizontalRecyclerView2 = findViewById(R.id.horizontalRecyclerView2)
        horizontalRecyclerView3 = findViewById(R.id.horizontalRecyclerView3)

        // Load all recipes from assets
        val allRecipes = loadRecipesFromAssets() ?: listOf()

        // Get random cuisine recipes and set titles for each RecyclerView
        val (randomRecipes1, cuisine1) = getRandomCuisineRecipes(allRecipes)
        sectionTitleTextView.text = cuisine1

        val (randomRecipes2, cuisine2) = getRandomCuisineRecipes(allRecipes.filterNot { it.cuisine == cuisine1 })
        sectionTitleTextView2.text = cuisine2

        val (randomRecipes3, cuisine3) = getRandomCuisineRecipes(allRecipes.filterNot { it.cuisine == cuisine1 || it.cuisine == cuisine2 })
        sectionTitleTextView3.text = cuisine3

        setupRecyclerView(horizontalRecyclerView, randomRecipes1)
        setupRecyclerView(horizontalRecyclerView2, randomRecipes2)
        setupRecyclerView(horizontalRecyclerView3, randomRecipes3)

         // MAKE SURE THIS IS COMMENTED OUT
         // OTHERWISE NO RECIPES WILL BE LIKED OR ADDED TO THE DATE
         // USEFUL FOR RESETTING SHARED PREFERENCE!
         //resetCalendar()
        // Register the broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(dateUpdateReceiver,
            IntentFilter("com.example.ce881app.DATE_UPDATED")
        )
    }

    // Broadcast receiver to handle date updates
    private val dateUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val updatedDate = intent.getStringExtra("updated_date")
            Log.d("DateUpdate", "Received update for date: $updatedDate")
            updatedDate?.let {
                // Update the adapter with the new date information
                refreshDaysAdapter(it)
            }
        }
    }

    /**
     * Sets up a RecyclerView with a horizontal layout manager and an adapter that displays a list of recipes.
     * When a recipe is clicked, it starts an activity to display detailed information about that recipe.
     */
    private fun setupRecyclerView(recyclerView: RecyclerView, recipes: List<Recipe>) {
        val adapter = HorizontalAdapter(ArrayList(recipes)) { recipe ->
            // Here you handle what happens when a recipe is clicked
            Log.d("Recipe Clicked", "Recipe ID: ${recipe.id}")
            val intent = Intent(this@HomeActivity, DetailedRecipeActivity::class.java).apply {
                putExtra("RECIPE_ID", recipe.id)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
}


    /**
     * Refreshes the days adapter to reflect changes in the selected date. This is used to update the UI
     * when a new date is selecred.
     */
    private fun refreshDaysAdapter(updatedDate: String) {
        // Now that daysList is a class property, you can access it here
        val index = daysList.indexOfFirst { it.date == updatedDate }
        if (index != -1) {
            daysAdapter.notifyItemChanged(index)
        }
    }

    // Unregisters the broadcast receiver when the activity is destroyed to prevent memory leaks.
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dateUpdateReceiver)
        super.onDestroy()
    }

    /**
     * Generates a lfist of DayInfo objects for each day of the current week. This list is used to populate
     * the days RecyclerView.
     */
    private fun generateWeekDays(): List<DayInfo> {
        val daysList = mutableListOf<DayInfo>()
        val calendar = Calendar.getInstance()

        // Set the calendar to the start of this week (you can adjust the first day of the week if needed)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        // SimpleDateFormat to format the date as needed
        val dayFormat = SimpleDateFormat("E", Locale.getDefault()) // "E" for day name
        val dateFormat = SimpleDateFormat("d", Locale.getDefault()) // "d" for day number

        // Generate DayInfo for each day of the current week
        for (i in 0..6) { // 0 to 6 for a week
            val dayName = dayFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)
            daysList.add(DayInfo(dayName, date))

            // Move the calendar to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysList
    }


     // Resets all shared preferences and refreshes the days RecyclerView to reflect any changes.
     // This method is mostly for development purposes and can be removed in the final version.
     fun resetCalendar() {
         // Reset shared preferences managed by SharedPreferencesUtil
         SharedPreferencesUtil.resetAllPreferences(this)

         // Also clear the 'RecipePrefs' shared preferences
         val recipePrefs = getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
         recipePrefs.edit().clear().apply()

         // Update the list and refresh the RecyclerView
         updateDaysListForReset()
     }



    private fun updateDaysListForReset() {
        daysList = daysList.map { DayInfo(it.dayName, it.date) } // Recreate the list with default values.
        daysAdapter.notifyDataSetChanged() // Notify the adapter of the data change.
    }

    /**
     * Selects a random cuisine from the list of recipes and returns a pair of the selected recipes
     * and the cuisine name. It is used to populate different sections of the UI with varied cuisines.
     */
    private fun getRandomCuisineRecipes(recipes: List<Recipe>, numberOfRecipes: Int = 10): Pair<List<Recipe>, String> {
        val groupedByCuisine = recipes.groupBy { it.cuisine }
        val randomCuisine = groupedByCuisine.keys.random()
        return Pair(groupedByCuisine[randomCuisine]?.shuffled()?.take(numberOfRecipes) ?: listOf(), randomCuisine)
    }

    /**
     * Loads recipes from a JSON file stored in the assets folder. This method is used to retrieve data
     * for the RecyclerViews displaying recipes.
     */
    private fun loadRecipesFromAssets(): List<Recipe>? {
        return try {
            val jsonString = assets.open("recipes.json").bufferedReader().use { it.readText() }
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