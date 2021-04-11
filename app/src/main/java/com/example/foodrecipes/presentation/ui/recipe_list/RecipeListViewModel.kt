package com.example.foodrecipes.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.interactors.recipe_list.RestoreRecipes
import com.example.foodrecipes.interactors.recipe_list.SearchRecipes
import com.example.foodrecipes.presentation.ui.recipe_list.RecipeListEvent.*
import com.example.foodrecipes.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

const val PAGE_SIZE = 30

const val STATE_KEY_PAGE = "recipe.state.page.key"
const val STATE_KEY_QUERY = "recipe.state.query.key"
const val STATE_KEY_LIST_POSITION = "recipe.state.query.list_position"
const val STATE_KEY_SELECTED_CATEGORY = "recipe.state.query.selected_category"

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
    private val restoreRecipes: RestoreRecipes,
    @Named("auth_token") private val token: String,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())

    val query = mutableStateOf("")

    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)

    var categoryScrollPosition = 0

    val loading = mutableStateOf(false)

    val page = mutableStateOf(1)

    var recipeListScrollPosition = 0

    init {
        savedStateHandle.get<Int>(STATE_KEY_PAGE)?.let { p ->
            setPage(p)
        }
        savedStateHandle.get<String>(STATE_KEY_QUERY)?.let { q ->
            setQuery(q)
        }
        savedStateHandle.get<Int>(STATE_KEY_LIST_POSITION)?.let { p ->
            setListScrollPosition(p)
        }
        savedStateHandle.get<FoodCategory>(STATE_KEY_SELECTED_CATEGORY)?.let { c ->
            setSelectedCategory(c)
        }
        if (recipeListScrollPosition != 0) {
            onTriggerEvent(RestoreStateEvent)
        } else {
            onTriggerEvent(NewSearchEvent)
        }
    }

    fun onTriggerEvent(event: RecipeListEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is NewSearchEvent -> {
                        newSearch()
                    }
                    is NextPageEvent -> {
                        nextPage()
                    }
                    is RestoreStateEvent -> {
                        restoreState()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "onTriggerEvent: Exception: ${e}, ${e.cause}")
            }
        }
    }

    private fun restoreState() {
        restoreRecipes.execute(
            page = page.value,
            query = query.value
        ).onEach { dataState ->

            loading.value = dataState.loading

            dataState.data?.let { list ->
                recipes.value = list
            }

            dataState.error?.let { error ->
                Log.e(TAG, "restoreState: $error", )
                //TODO("Handle Error")
            }

        }.launchIn(viewModelScope)
    }

    // Use case #1
    private fun newSearch() {
        Log.d(TAG, "newSearch: query: ${query.value}, page: ${page.value}")
        resetSearchState()

        searchRecipes.execute(
            token = token,
            page = page.value,
            query = query.value
        ).onEach { dataState ->

            loading.value = dataState.loading
            
            dataState.data?.let { list ->
                recipes.value = list
            }
            
            dataState.error?.let { error ->
                Log.e(TAG, "newSearch: $error", )
                //TODO("Handle Error")
            }

        }.launchIn(viewModelScope)
    }

    // Use case #2
    private fun nextPage() {
        // prevent duplicate events due to recompose happening to quickly
        if (recipeListScrollPosition + 1 >= (page.value * PAGE_SIZE)) {
            incrementPageNumber()
            Log.d(TAG, "nextPage: triggered: ${page.value}")

            if (page.value > 1) {
                searchRecipes.execute(
                    token = token,
                    page = page.value,
                    query = query.value
                ).onEach { dataState ->

                    loading.value = dataState.loading

                    dataState.data?.let { list ->
                        appendRecipes(list)
                    }

                    dataState.error?.let { error ->
                        Log.e(TAG, "nextPage: $error", )
                        //TODO("Handle Error")
                    }

                }.launchIn(viewModelScope)
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
        setPage(page.value + 1)
    }

    fun onChangeRecipeScrollPosition(position: Int) {
        setListScrollPosition(position = position)
    }

    private fun clearSelectedCategory() {
        setSelectedCategory(null)
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
        setQuery(query)
    }

    fun onSelectedCategoryChanged(category: String) {
        val newCategory = getFoodCategory(category)
        setSelectedCategory(newCategory)
        onQueryChanged(category)
    }

    fun onChangeCategoryScrollPosition(position: Int) {
        categoryScrollPosition = position
    }

    private fun setListScrollPosition(position: Int) {
        recipeListScrollPosition = position
        savedStateHandle.set(STATE_KEY_LIST_POSITION, position)
    }

    private fun setPage(page: Int) {
        this.page.value = page
        savedStateHandle.set(STATE_KEY_PAGE, page)
    }

    private fun setSelectedCategory(category: FoodCategory?) {
        selectedCategory.value = category
        savedStateHandle.set(STATE_KEY_SELECTED_CATEGORY, category)
    }

    private fun setQuery(query: String) {
        this.query.value = query
        savedStateHandle.set(STATE_KEY_QUERY, query)
    }
}