package com.example.jokeapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jokeapp.ui.screens.categories.CategorySelectScreen
import com.example.jokeapp.ui.screens.jokes.JokeListScreen


/**
 * Enum class representing the different destinations in the app.
 * @param title The string resource ID for the title of the destination.
 */
enum class JokeAppDestinations(@StringRes val title: Int) {
    JOKE_CATEGORIES(R.string.category_screen_title),
    JOKES_LIST(R.string.jokes_screen_title),
}

/**
 * The main entry point of the app, which sets up the navigation and top bar.
 * @param navController The NavHostController for managing navigation.
 */
@Composable
fun JokeApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    // substringBefore("/") is used to get the screen name from the route without parameters
    val screenName = backStackEntry?.destination?.route?.substringBefore("/")
    // Get the title for the current screen based on the screen name.
    // If the screen name is JOKES_LIST, the category is appended to the title.
    val currentScreenTitle = stringResource(
        JokeAppDestinations.valueOf(screenName ?: JokeAppDestinations.JOKE_CATEGORIES.name).title
    ) + if (screenName == JokeAppDestinations.JOKES_LIST.name)
        " - ${backStackEntry?.arguments?.getString("category")}"
    else ""


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            JokeAppTopBar(
                title = currentScreenTitle,
                showNavigationIcon = navController.previousBackStackEntry != null
            ) {
                navController.navigateUp()
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = JokeAppDestinations.JOKE_CATEGORIES.name,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(JokeAppDestinations.JOKE_CATEGORIES.name) {
                // Start screen with category selection.
                // onCategorySelected will navigate to the jokes list screen with a selected category.
                CategorySelectScreen(
                    onCategorySelected = { category ->
                        navController.navigate("${JokeAppDestinations.JOKES_LIST.name}/$category")
                    }
                )
            }
            composable(
                "${JokeAppDestinations.JOKES_LIST.name}/{category}",
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        nullable = false // category cannot be null
                    }
                )
            ) { backStackEntry ->
                // Jokes list screen with the selected category passed as an argument via a ViewModel.
                JokeListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeAppTopBar(
    title: String,
    showNavigationIcon: Boolean,
    onNavigateUp: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
        navigationIcon = {
            if (showNavigationIcon)
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
        })
}