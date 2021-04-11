package com.example.foodrecipes.cache.model

import com.example.foodrecipes.domain.model.Recipe
import com.example.foodrecipes.domain.model.util.DomainMapper
import com.example.foodrecipes.util.DateUtils
import java.lang.StringBuilder

class RecipeEntityMapper : DomainMapper<RecipeEntity, Recipe> {

    override fun mapToDomainModel(model: RecipeEntity): Recipe {
        return Recipe(
            id = model.id,
            title = model.title,
            featuredImage = model.featuredImage,
            rating = model.rating,
            publisher = model.publisher,
            sourceUrl = model.sourceUrl,
            ingredients = convertIngredientsToList(model.ingredients),
            dateAdded = DateUtils.longToDate(model.dateAdded),
            dateUpdated = DateUtils.longToDate(model.dateUpdated),
        )
    }

    override fun mapFromDomainModel(domainModel: Recipe): RecipeEntity {
        return RecipeEntity(
            id = domainModel.id,
            title = domainModel.title,
            featuredImage = domainModel.featuredImage,
            rating = domainModel.rating,
            publisher = domainModel.publisher,
            sourceUrl = domainModel.sourceUrl,
            ingredients = convertIngredientListToString(domainModel.ingredients),
            dateAdded = DateUtils.dateToLong(domainModel.dateAdded),
            dateUpdated = DateUtils.dateToLong(domainModel.dateUpdated),
            dateCached = DateUtils.dateToLong(DateUtils.createTimestamp())
        )
    }

    private fun convertIngredientListToString(ingredients: List<String>): String {
        val ingredientsString = StringBuilder()
        for (ingredient in ingredients) {
            ingredientsString.append("$ingredient,")
        }
        return ingredientsString.toString()
    }

    // "carrot, chicken, etc.."
    private fun convertIngredientsToList(ingredientString: String?): List<String> {
        val list: ArrayList<String> = ArrayList()
        ingredientString?.let {
            for (ingredient in it.split(",")) {
                list.add(ingredient)
            }
        }
        return list
    }
}