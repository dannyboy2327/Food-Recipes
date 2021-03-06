package com.example.foodrecipes.interactors.recipe_list

import android.util.Log
import com.example.foodrecipes.cache.RecipeDao
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.domain.data.DataState
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.util.RECIPE_PAGINATION_PAGE_SIZE
import com.example.foodrecipes.util.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class RestoreRecipes(
    private val recipeDao: RecipeDao,
    private val entityMapper: RecipeEntityMapper,
) {

    fun execute(
        page: Int,
        query: String,
    ): Flow<DataState<List<Recipe>>> = flow {
        try {
            emit(DataState.loading<List<Recipe>>())

            delay(1000)

            val cacheResult = if (query.isBlank()) {
                recipeDao.restoreAllRecipes(
                    pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                    page = page
                )
            } else {
                recipeDao.restoreRecipes(
                    query = query,
                    pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                    page = page
                )
            }

            val list = entityMapper.fromEntityList(cacheResult)
            emit(DataState.success(list))

        } catch (e: Exception) {
            Log.e(TAG, "execute: ${e.message}")
            emit(DataState.error<List<Recipe>>(e.message?: "Unknown error"))
        }
    }
}