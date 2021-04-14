package com.example.foodrecipes.cache

import com.example.foodrecipes.cache.model.RecipeEntity

class RecipeDaoFake(
    private val appDatabaseFake: AppDatabaseFake
): RecipeDao {

    override suspend fun insertRecipe(recipe: RecipeEntity): Long {
        appDatabaseFake.recipes.add(recipe)
        return 1 // return success
    }

    override suspend fun insertRecipes(recipes: List<RecipeEntity>): LongArray {
        appDatabaseFake.recipes.addAll(recipes)
        return LongArray(1) // return success
    }

    override suspend fun getRecipeById(id: Int): RecipeEntity? {
        return appDatabaseFake.recipes.find { it.id  == id }
    }

    override suspend fun deleteRecipes(ids: List<Int>): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllRecipes() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(primaryKey: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun searchRecipes(
        query: String,
        page: Int,
        pageSize: Int
    ): List<RecipeEntity> {
        return appDatabaseFake.recipes
    }

    override suspend fun getAllRecipes(page: Int, pageSize: Int): List<RecipeEntity> {
        return appDatabaseFake.recipes
    }

    override suspend fun restoreRecipes(
        query: String,
        page: Int,
        pageSize: Int
    ): List<RecipeEntity> {
        return appDatabaseFake.recipes
    }

    override suspend fun restoreAllRecipes(page: Int, pageSize: Int): List<RecipeEntity> {
        return appDatabaseFake.recipes
    }
}