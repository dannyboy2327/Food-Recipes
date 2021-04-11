package com.example.foodrecipes.interactors.recipe_list

import com.example.foodrecipes.cache.RecipeDao
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.domain.data.DataState
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.network.model.RecipeDtoMapper
import com.example.foodrecipes.network.model.RecipeService
import com.example.foodrecipes.util.RECIPE_PAGINATION_PAGE_SIZE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class SearchRecipes(
    private val recipeDao: RecipeDao,
    private val recipeService: RecipeService,
    private val entityMapper: RecipeEntityMapper,
    private val dtoMapper: RecipeDtoMapper,
) {
    fun execute(
        token: String,
        page: Int,
        query: String,
    ): Flow<DataState<List<Recipe>>> = flow {
        try {
            emit(DataState.loading<List<Recipe>>())

            // just to show pagination/progress bar
            delay(1000)

            // TODO("Check if there is an internet connection")
            val recipes = getRecipesFromNetwork(
                token = token,
                page = page,
                query = query
            )

            // insert into the cache
            recipeDao.insertRecipes(entityMapper.toEntityList(recipes))

            // query the cache
            val cacheResult = if (query.isBlank()) {
                recipeDao.getAllRecipes(
                    pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                    page = page,
                )
            } else {
                recipeDao.searchRecipes(
                    query = query,
                    pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                    page = page,
                )
            }

            // emit List<Recipe> from the cache
            val list = entityMapper.fromEntityList(cacheResult)

            emit(DataState.success(list))

        } catch (e: Exception) {
            emit(DataState.error<List<Recipe>>(e.message?: "Unknown error"))
        }
    }

    // This can throw a exception if there is no network connection
    private suspend fun getRecipesFromNetwork(
        token: String,
        page: Int,
        query: String,
    ): List<Recipe> {
        return dtoMapper.toDomainList(
            recipeService.search(
                token = token,
                page = page,
                query = query
            ).recipes
        )
    }
}