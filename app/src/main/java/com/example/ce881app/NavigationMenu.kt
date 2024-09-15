package com.example.ce881app

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageButton

/**
 * NavigationMenu simplifies the navigation across different activities.
 * It  sets up click listeners for each button to navigate to
 * specific activities such as DinnerSwipeActivity, CalendarDinner, and LikedPageActivity.
 */
class NavigationMenu(private val context: Context, rootView: View) {
    private val swipeButton: ImageButton
    private val homeButton: ImageButton
    private val likedButton: ImageButton

    init {
        // Initialise buttons
        swipeButton = rootView.findViewById(R.id.nav_button1) as ImageButton
        homeButton = rootView.findViewById(R.id.nav_button2) as ImageButton
        likedButton = rootView.findViewById(R.id.nav_button3)as ImageButton

        // Set click listeners
        setupButtonListeners()
    }

    private fun setupButtonListeners() {

        swipeButton.setOnClickListener { // Navigate to Swipe Activity
            context.startActivity(Intent(context, DinnerSwipeActivity::class.java))
        }
        homeButton.setOnClickListener { // Navigate to Home Activity
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
        likedButton.setOnClickListener { // Navigate to Liked Activity
            context.startActivity(Intent(context, LikedPageActivity::class.java))
        }
    }
}

