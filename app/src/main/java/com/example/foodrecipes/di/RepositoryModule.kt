package com.example.foodrecipes.di

import com.example.foodrecipes.network.model.RecipeDtoMapper
import com.example.foodrecipes.network.model.RecipeService
import com.example.foodrecipes.repository.RecipeRepository
import com.example.foodrecipes.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRecipeRepository(
        recipeService: RecipeService,
        recipeDtoMapper: RecipeDtoMapper
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            recipeService,
            recipeDtoMapper
        )
    }
}