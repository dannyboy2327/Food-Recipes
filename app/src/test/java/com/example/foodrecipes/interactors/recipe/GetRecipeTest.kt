package com.example.foodrecipes.interactors.recipe

import com.example.foodrecipes.cache.AppDatabaseFake
import com.example.foodrecipes.cache.RecipeDaoFake
import com.example.foodrecipes.cache.model.RecipeEntityMapper
import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.interactors.recipe_list.SearchRecipes
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

class GetRecipeTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl
    private val appDatabaseFake = AppDatabaseFake()
    private val dummy_token = "faefeskfwkdakf"
    private val dummy_query = "eh doesn't matter"


    // System in test
    private lateinit var getRecipe: GetRecipe
    private val RECIPE_ID = 1551

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
        getRecipe = GetRecipe(
            recipeDao = recipeDaoFake,
            entityMapper = entityMapper,
            recipeService = recipeService,
            recipeDtoMapper = dtoMapper,
        )
    }

    /**
     * 1. Get some recipes from the network and insert into cache
     * 2. Try to retrieve recipes by their specific recipe id
     */
    @Test
    fun getRecipesFromNetwork_getRecipeById(): Unit = runBlocking {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockWebServerResponses.recipeListResponse)
        )

        // confirm the cache is empty to start
        assert(recipeDaoFake.getAllRecipes(1, 30).isEmpty())

        // get recipes from network and inset into the cache
        searchRecipes.execute(dummy_token, 1, dummy_query, true).toList()

        // confirm the cache is no longer empty
        assert(recipeDaoFake.getAllRecipes(1, 30).isNotEmpty())

        // run use case
        val recipeAsFlow = getRecipe.execute(RECIPE_ID, dummy_token, true).toList()

        // first emission should be loading
        assert(recipeAsFlow[0].loading)

        // second emission should be the recipe
        val recipe = recipeAsFlow[1].data
        assert(recipe?.id == RECIPE_ID)

        // confirm it is actually a recipe object
        assert(recipe is Recipe)

        // loading should be false now
        assert(!recipeAsFlow[1].loading)
    }

    /**
     * 1. Try to get a recipe that does not exist in the cache
     * Result should be:
     * 1. Recipe is retrieved from network and inserted into cache
     * 2. Recipe is returned as flow from cache
     */
    @Test
    fun attemptGetNullRecipeFromCache_getRecipeById(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(MockWebServerResponses.recipeWithId1551)
        )

        // confirm the cache is empty to start
        assert(recipeDaoFake.getAllRecipes(1, 30).isEmpty())

        // run use case
        val recipeAsFlow = getRecipe.execute(RECIPE_ID, dummy_token, true).toList()

        // first emission should be `loading`
        assert(recipeAsFlow[0].loading)

        // second emission should be the recipe
        val recipe = recipeAsFlow[1].data
        assert(recipe?.id == RECIPE_ID)

        // confirm the recipe is in the cache now
        assert(recipeDaoFake.getRecipeById(RECIPE_ID)?.id == RECIPE_ID)

        // confirm it is actually a Recipe object
        assert(recipe is Recipe)

        // 'loading' should be false now
        assert(!recipeAsFlow[1].loading)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}