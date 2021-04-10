package com.example.foodrecipes.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodrecipes.cache.RecipeDao
import com.example.foodrecipes.cache.model.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        val DATABASE_NAME = "recipe_db"
    }
}