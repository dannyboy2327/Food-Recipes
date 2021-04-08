package com.example.foodrecipes.presentation.ui.recipe_list

sealed class RecipeListEvent {

    object NewSearchEvent: RecipeListEvent()

    object NextPageEvent: RecipeListEvent()

    // Restore after process death
    object RestoreStateEvent: RecipeListEvent()
}