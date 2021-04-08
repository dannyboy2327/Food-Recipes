package com.example.foodrecipes.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.repository.RecipeRepository
import com.example.foodrecipes.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Named

const val PAGE_SIZE = 30

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val repository: RecipeRepository,
    @Named("auth_token") private val token: String
): ViewModel() {

    val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())

    val query = mutableStateOf("")

    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)

    var categoryScrollPosition = 0

    val loading = mutableStateOf(false)

    val page = mutableStateOf(1)

    var recipeListScrollPosition = 0

    init {
        newSearch()
    }

    // Use case #1
    fun newSearch() {
        viewModelScope.launch {

            loading.value = true

            resetSearchState()

            delay(2000)

            val result = repository.search(
                token = token,
                page = 1,
                query = query.value
            )
            recipes.value = result
            loading.value = false
        }
    }

    // Use case #2
    fun nextPage() {
        viewModelScope.launch {
            // prevent duplicate events due to recompose happening to quickly
            if (recipeListScrollPosition + 1 >= (page.value * PAGE_SIZE)) {
                loading.value = true
                incrementPageNumber()
                Log.d(TAG, "nextPage: triggered: ${page.value}")


                delay(1000)
                if (page.value > 1) {
                    val result = repository.search(
                        token = token,
                        page = page.value,
                        query = query.value
                    )
                    Log.d(TAG, "nextPage:${result}")
                    appendRecipes(result)
                }
                loading.value = false
            }
        }
    }

    /**
     *  Append new recipes to teh current list of recipes
     */
    private fun appendRecipes(recipes: List<Recipe>) {
        val current = ArrayList(this.recipes.value)
        current.addAll(recipes)
        this.recipes.value = current
    }

    private fun incrementPageNumber() {
        page.value = page.value + 1
    }

    fun onChangeRecipeScrollPosition(position: Int) {
        recipeListScrollPosition = position
    }

    private fun clearSelectedCategory() {
        selectedCategory.value = null
    }

    private fun resetSearchState() {
        recipes.value = listOf()
        page.value = 1
        onChangeRecipeScrollPosition(0)
        if (selectedCategory.value?.value != query.value) {
            clearSelectedCategory()
        }
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onSelectedCategoryChanged(category: String) {
        val newCategory = getFoodCategory(category)
        selectedCategory.value = newCategory
        onQueryChanged(category)
    }

    fun onChangeCategoryScrollPosition(position: Int) {
        categoryScrollPosition = position
    }
}