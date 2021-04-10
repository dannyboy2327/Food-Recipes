package com.example.foodrecipes.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.foodrecipes.presentation.navigation.Screen
import com.example.foodrecipes.presentation.ui.recipe.RecipeDetailScreen
import com.example.foodrecipes.presentation.ui.recipe.RecipeViewModel
import com.example.foodrecipes.presentation.ui.recipe_list.RecipeListScreen
import com.example.foodrecipes.presentation.ui.recipe_list.RecipeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.RecipeList.route
            ) {
                composable(route = Screen.RecipeList.route) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: RecipeListViewModel = viewModel("RecipeListViewModel", factory)
                    RecipeListScreen(
                        isDarkTheme = (application as BaseApplication).isDark.value,
                        onToggleTheme = { (application as BaseApplication)::toggleLightTheme },
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel
                    )
                }
                composable(
                    route = Screen.RecipeDetail.route + "/{recipeId}",
                    arguments = listOf(navArgument("recipeId") {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: RecipeViewModel = viewModel("RecipeDetailViewModel", factory)
                    RecipeDetailScreen(
                        isDarkTheme = (application as BaseApplication).isDark.value,
                        recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}