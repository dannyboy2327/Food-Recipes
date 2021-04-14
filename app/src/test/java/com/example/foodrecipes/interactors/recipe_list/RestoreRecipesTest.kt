package com.example.foodrecipes.interactors.recipe_list

import com.example.foodrecipes.cache.AppDatabaseFake
import com.example.foodrecipes.cache.RecipeDaoFake
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.network.data.MockWebServerResponses
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

class RestoreRecipesTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl
    private val appDatabaseFake = AppDatabaseFake()
    private val dummy_token = "faefeskfwkdakf"
    private val dummy_query = "eh doesn't matter"


    // System in test
    private lateinit var restoreRecipes: RestoreRecipes

    // dependencies
    private lateinit var searchRecipes: SearchRecipes
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

        // instantiate the system in test
        restoreRecipes = RestoreRecipes(
            recipeDao = recipeDaoFake,
            entityMapper = entityMapper,
        )
    }

    /**
     *  1. Get some recipes from the network and insert into the cache
     *  2. Restore and show recipes are retrieved from cache
     */
    @Test
    fun getRecipesFromNetwork_restoreFromCache(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockWebServerResponses.recipeListResponse)
        )

        assert(recipeDaoFake.getAllRecipes(1, 30).isEmpty())

        searchRecipes.execute(dummy_token, 1, dummy_query, true).toList()

        assert(recipeDaoFake.getAllRecipes(1, 30).isNotEmpty())

        // Run our use case
        val flowItems = restoreRecipes.execute(1, dummy_query).toList()

        assert(flowItems[0].loading)

        val recipes = flowItems[1].data
        assert(recipes?.size?: 0 > 0)

        assert(recipes?.get(0) is Recipe)

        assert(!flowItems[1].loading)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}