package com.example.foodrecipes.interactors.recipe_list

import com.example.foodrecipes.cache.AppDatabaseFake
import com.example.foodrecipes.cache.RecipeDaoFake
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.network.data.MockWebServerResponses.recipeListResponse
import com.example.foodrecipes.network.model.RecipeDtoMapper
import com.example.foodrecipes.network.model.RecipeService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class SearchRecipeTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl
    private val appDatabaseFake = AppDatabaseFake()
    private val dummy_token = "faefeskfwkdakf"
    private val dummy_query = "eh doesn't matter"


    // System in test
    private lateinit var searchRecipes: SearchRecipes

    // dependencies
    private lateinit var recipeService: RecipeService
    private lateinit var recipeDaoFake: RecipeDaoFake
    private val dtoMapper = RecipeDtoMapper()
    private val entityMapper = RecipeEntityMapper()

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")
        recipeService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(RecipeService::class.java)

        recipeDaoFake = RecipeDaoFake(appDatabaseFake)

        // instantiate the system in test
        searchRecipes = SearchRecipes(
            recipeDao = recipeDaoFake,
            recipeService = recipeService,
            entityMapper = entityMapper,
            dtoMapper = dtoMapper,
        )
    }

    /**
     *  1. Are the recipes retrieved from the network
     *  2. Are the recipes inserted into the cache
     *  3. Are the recipes then emitted as a FLOW from the cache to the UI
     */
    @Test
    fun getRecipesFromNetwork_emitRecipesFromCache(): Unit = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(recipeListResponse)
        )

        // Confirm cache is empty to start
        assert(recipeDaoFake.getAllRecipes(1, 30).isEmpty())

        val flowItems = searchRecipes.execute(dummy_token, 1, dummy_query, true)
            .toList()

        // Confirm cache is  no longer empty
        assert(recipeDaoFake.getAllRecipes(1, 30).isNotEmpty())

        // First emission should be loading
        assert(flowItems[0].loading)

        // Second emission should be list of recipes
        val recipes = flowItems[1].data
        assert(recipes?.size?: 0 > 0 )

        // Confirm they are actually Recipe objects
        assert(recipes?.get(index = 15) is Recipe)

        // Ensure loading is false now
        assert(!flowItems[1].loading)
    }

    @Test
    fun getRecipesFromNetwork_emitError() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .setBody("{}")
        )

        val flowItems = searchRecipes.execute(dummy_token, 1 , dummy_query, true).toList()

        assert(flowItems[0].loading)

        val error = flowItems[1].error
        assert(error != null)

        assert(!flowItems[1].loading)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

}