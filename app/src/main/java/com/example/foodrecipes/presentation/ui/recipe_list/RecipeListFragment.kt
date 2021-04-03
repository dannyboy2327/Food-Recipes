package com.example.foodrecipes.presentation.ui.recipe_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.foodrecipes.presentation.components.FoodCategoryChip
import com.example.foodrecipes.presentation.components.RecipeCard
import com.example.foodrecipes.presentation.components.SearchAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeListFragment: Fragment() {

    private val viewModel: RecipeListViewModel by viewModels()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        return ComposeView(requireContext()).apply {
            setContent {

                val recipes = viewModel.recipes.value

                val query = viewModel.query.value

                val selectedCategory = viewModel.selectedCategory.value

                Column {
                    
                    SearchAppBar(
                        query = query,
                        onQueryChanged = viewModel::onQueryChanged,
                        onExecuteSearch = viewModel::newSearch,
                        scrollPosition = viewModel.categoryScrollPosition,
                        selectedCategory = selectedCategory,
                        onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                        onChangeCategoryScrollPosition = viewModel::onChangeCategoryScrollPosition
                    )
                    LazyColumn{
                        itemsIndexed(
                            items = recipes
                        ) { index, recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}