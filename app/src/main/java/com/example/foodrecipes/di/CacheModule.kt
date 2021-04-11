package com.example.foodrecipes.di

import androidx.room.Room
import com.example.foodrecipes.cache.RecipeDao
import com.example.foodrecipes.cache.database.AppDatabase
import com.example.foodrecipes.cache.database.AppDatabase.Companion.DATABASE_NAME
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.presentation.BaseApplication
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideDb(app: BaseApplication): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRecipeDao(app: AppDatabase): RecipeDao {
        return app.recipeDao()
    }

    @Singleton
    @Provides
    fun provideCacheRecipeMapper(): RecipeEntityMapper{
        return RecipeEntityMapper()
    }
}