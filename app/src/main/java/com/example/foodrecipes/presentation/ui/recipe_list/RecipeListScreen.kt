package com.example.foodrecipes.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.findNavController
import com.example.foodrecipes.presentation.components.RecipeList
import com.example.foodrecipes.presentation.components.SearchAppBar
import com.example.foodrecipes.presentation.theme.AppTheme
import com.example.foodrecipes.util.TAG
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun RecipeListScreen(
    isDarkTheme: Boolean,
    isNetworkAvailable: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToRecipeDetailScreen: (String) -> Unit,
    viewModel: RecipeListViewModel,
) {

    Log.d(TAG, "RecipeListScreen: $viewModel")

    val recipes = viewModel.recipes.value

    val query = viewModel.query.value

    val selectedCategory = viewModel.selectedCategory.value

    val loading = viewModel.loading.value

    val page = viewModel.page.value

    val dialogQueue = viewModel.dialogQueue

    val scaffoldState = rememberScaffoldState()

    AppTheme(
        darkTheme = isDarkTheme,
        isNetworkAvailable = isNetworkAvailable,
        displayProgressBar = loading,
        scaffoldState= scaffoldState,
        dialogQueue = dialogQueue.queue.value,
    ) {
        Scaffold(
            topBar = {
                SearchAppBar(
                    query = query,
                    onQueryChanged = viewModel::onQueryChanged,
                    onExecuteSearch = {
                        viewModel.onTriggerEvent(RecipeListEvent.NewSearchEvent)
                    },
                    scrollPosition = viewModel.categoryScrollPosition,
                    selectedCategory = selectedCategory,
                    onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                    onChangeCategoryScrollPosition = viewModel::onChangeCategoryScrollPosition,
                    onToggleTheme = {
                        onToggleTheme()
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ){
            RecipeList(
                recipes = recipes,
                loading = loading,
                onChangeRecipeScrollPosition = viewModel::onChangeRecipeScrollPosition,
                page = page,
                onTriggerNextPage = {
                    viewModel.onTriggerEvent(RecipeListEvent.NextPageEvent)
                },
                onNavigateToRecipeDetailScreen = onNavigateToRecipeDetailScreen
            )
        }
    }
}