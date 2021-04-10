package com.example.foodrecipes.presentation.components

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodrecipes.R
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.presentation.components.util.SnackbarController
import com.example.foodrecipes.presentation.navigation.Screen
import com.example.foodrecipes.presentation.ui.recipe_list.PAGE_SIZE
import com.example.foodrecipes.presentation.ui.recipe_list.RecipeListEvent
import kotlinx.coroutines.launch

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    loading: Boolean,
    onChangeRecipeScrollPosition: (Int) -> Unit,
    page: Int,
    onTriggerNextPage: () -> Unit,
    onNavigateToRecipeDetailScreen: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colors.surface)
    ){
        if (loading && recipes.isEmpty()) {
            LoadingRecipeListShimmer(imageHeight = 250.dp)
        } else if (recipes.isEmpty()) {
            NothingHere()
        } else {
            LazyColumn{
                itemsIndexed(
                    items = recipes
                ) { index, recipe ->
                    onChangeRecipeScrollPosition(index)
                    if ((index + 1) >= (page * PAGE_SIZE) && !loading) {
                        onTriggerNextPage()
                    }
                    RecipeCard(
                        recipe = recipe,
                        onClick = {
                            val route = Screen.RecipeDetail.route + "/${recipe.id}"
                            onNavigateToRecipeDetailScreen(route)
                        }
                    )
                }
            }
        }
    }
}