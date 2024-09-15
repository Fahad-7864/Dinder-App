package com.example.ce881app

// Data class representing a recipe.
data class Recipe(
    val id: Int,
    val imageRes: String, // This will hold the resource name as a String
    val title: String,
    val cuisine: String, // "Indian" or "Chinese" and so on
    val prepTime: String,
    val spiceLevel: Int, // Number of chili peppers, e.g., 1 for mild, 4 for hot
    val serves: Int,
    val isVegetarian: Boolean,
    val ingredients: List<String>,
    val instructions: List<String>,
    var isLiked: Boolean = false // This will be used to track if the recipe is liked by the user

)
//  Wrapper class for a list of recipes. Useful for JSON.
data class RecipesWrapper(
    val recipes: List<Recipe>
)
