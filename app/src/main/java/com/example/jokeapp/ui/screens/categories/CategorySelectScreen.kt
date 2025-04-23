package com.example.jokeapp.ui.screens.categories


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokeapp.JokeAppViewModelProvider
import com.example.jokeapp.ui.reusableComponents.ErrorMessage
import com.example.jokeapp.ui.reusableComponents.LoadingIndicator

/**
 * A composable function that displays a list of joke categories.
 * It uses a ViewModel to manage the state of the categories.
 * @param viewModel The ViewModel that provides the categories and manages state.
 * @param modifier The modifier to be applied to the composable.
 * @param onCategorySelected A callback function that is called when a category is selected.
 */
@Composable
fun CategorySelectScreen(
    modifier: Modifier = Modifier,
    viewModel: CategorySelectViewModel = viewModel(factory = JokeAppViewModelProvider.Factory),
    onCategorySelected: (String) -> Unit
) {
    // Observe the UI state from the ViewModel.
    // The exposed uiState in the CategorySelectViewModel is a StateFlow,
    // which is a type of observable state holder.
    // Any changes to the state will be automatically reflected in the UI.
    val uiState by viewModel.uiState.collectAsState()
    // The uiState is of type CategoryScreenUiState, which can be Loading, Success, or Error.
    // By using the when expression with the sealed interface approach,
    // we can handle each state accordingly in a readable way.
    when (uiState) {
        is CategoryScreenUiState.Loading -> {
            // Show loading indicator when the state is Loading
            LoadingIndicator(
                modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        is CategoryScreenUiState.Success -> {
            // Show the list of categories when the state is Success
            // The categories are extracted from the uiState after casting it to the Success subclass.
            val categories = (uiState as CategoryScreenUiState.Success).categories
            LazyColumn(modifier = modifier) {
                items(items = categories) { category ->
                    CategoryItem(
                        categoryName = category,
                        onCategoryClick = {
                            onCategorySelected(category)
                        }
                    ) }

            }
        }

        is CategoryScreenUiState.Error -> {
            // Handle error state - show error message to the user
            ErrorMessage(
                errorTitle = stringResource(
                    id = (uiState as CategoryScreenUiState.Error).titleRes
                ),
                errorMessage = (uiState as CategoryScreenUiState.Error).message,
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

        }
    }
}

/**
 * A composable function that displays a single category item.
 * It is used within the [CategorySelectScreen] to represent each category in the list.
 * @param categoryName The name of the category to be displayed.
 * @param modifier The modifier to be applied to the composable.
 * @param onCategoryClick A callback function that is called when the category is clicked.
 */
@Composable
fun CategoryItem(categoryName: String, modifier: Modifier = Modifier, onCategoryClick: () -> Unit) {
    Surface(
        onClick = {
            onCategoryClick()
        },
        modifier = modifier
            .padding(8.dp),
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Display the category name abbreviation in a circular shape with a border
            Text(
                text = categoryName.take(2),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .wrapContentSize(Alignment.Center)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = categoryName,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}



