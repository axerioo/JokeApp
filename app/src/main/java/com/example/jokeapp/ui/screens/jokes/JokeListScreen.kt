package com.example.jokeapp.ui.screens.jokes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokeapp.JokeAppViewModelProvider

import com.example.jokeapp.R
import com.example.jokeapp.extensions.substringBeforeNthDelimiter
import com.example.jokeapp.model.Joke
import com.example.jokeapp.retrofit.JokeType

import com.example.jokeapp.ui.reusableComponents.ChipGroupReflow
import com.example.jokeapp.ui.reusableComponents.ErrorMessage
import com.example.jokeapp.ui.reusableComponents.ExposedDropdownMenu
import com.example.jokeapp.ui.reusableComponents.InfoMessage
import com.example.jokeapp.ui.reusableComponents.LoadingIndicator
import com.example.jokeapp.ui.reusableComponents.SingleChoiceSegmentedButton

/**
 * A composable function that displays a list of jokes based on a query
 * and allows the user to select a joke to view its details.
 * It uses a ViewModel to manage the state of the query, jokes list and the selected joke.
 * @param modifier The modifier to be applied to the composable.
 * @param viewModel The ViewModel that provides the jokes and manages state.
 */
@Composable
fun JokeListScreen(
    modifier: Modifier = Modifier,
    viewModel: JokeListViewModel = viewModel(factory = JokeAppViewModelProvider.Factory)
) {
    val selectedJoke by viewModel.selectedJoke.collectAsState()
    // Show the selected joke dialog when selectedJoke != null
    selectedJoke?.let { joke ->
        JokeDialog(
            joke = joke,
            onConfirm = { viewModel.clearSelectedJoke() },
            onDismiss = { viewModel.clearSelectedJoke() }
        )
    }

    // Observe the UI state from the ViewModel. We observe the uiState used to change the
    // UI based on the state of data fetch operation. selectedJoke is used to trigger recomposition
    // that will show/hide a details dialog
    val uiState by viewModel.uiState.collectAsState()


    // The main part of the screen
    Column(modifier = modifier) {
        // This view allows the user to select options for the joke query.
        // viewModel is passed to configure the view and manage the state.
        JokeQueryCardView(
            viewModel = viewModel,
            onGetJokesClicked = {
                // respond to the button click
                viewModel.fetchJokes()
            },
        )
        // The uiState is of type JokeListUiState, which can be
        // Initial, Loading, Success, or Error.
        when (uiState) {
            is JokeListUiState.Initial -> {
                // Initial state - show a welcome message
                InfoMessage(
                    message = stringResource(R.string.press_button_to_get_jokes),
                    modifier = Modifier.fillMaxSize()
                )
            }

            is JokeListUiState.Loading -> {
                // Show loading indicator - after user pressed the button
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            is JokeListUiState.Success -> {
                // Show the list of jokes when the response from the API is successful
                // The jokes are extracted from the uiState after casting it to
                // the Success subclass.
                val jokes = (uiState as JokeListUiState.Success).jokes
                // Display the list or a message if the list is empty
                if (jokes.isNotEmpty()) {
                    JokeList(
                        jokes = jokes,
                        onJokeClick = { joke ->
                            // Handle joke click - show the joke details dialog
                            viewModel.selectJoke(joke)
                        }
                    )
                } else {
                    InfoMessage(
                        message = stringResource(R.string.no_jokes_found),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            is JokeListUiState.Error -> {
                // Handle error state - show error message to the user
                val errorMessage = (uiState as JokeListUiState.Error).message
                val errorTitle = stringResource((uiState as JokeListUiState.Error).titleRes)
                ErrorMessage(
                    errorTitle = errorTitle,
                    errorMessage = errorMessage,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


/**
 * A composable function that displays a list of jokes in a card layout.
 * @param jokes The list of jokes to be displayed.
 * @param modifier The modifier to be applied to the composable.
 * @param onJokeClick A callback function that is called when a joke is clicked.
 */
@Composable
fun JokeList(
    jokes: List<Joke>,
    modifier: Modifier = Modifier,
    onJokeClick: (Joke) -> Unit
) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(jokes) { joke ->
                JokeListItem(
                    joke = joke,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onJokeClick(joke)
                }
            }
        }
    }
}

/**
 * A composable function that displays a single joke item in a card layout.
 * @param joke The joke to be displayed.
 * @param modifier The modifier to be applied to the composable.
 * @param onClick A callback function that is called when the joke item is clicked.
 */
@Composable
fun JokeListItem(joke: Joke, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val cardColors = CardDefaults.cardColors()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(4.dp)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    color = Color.Green
                )
            ),
        shape = MaterialTheme.shapes.medium,
        color = cardColors.containerColor
    ) {

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                val jokeText = if (joke.type == JokeType.single.name) joke.joke else joke.setup
                Text(
                    // Shorten the joke text to fit in the card
                    // use substringBeforeNthDelimiter to get the first 3 words
                    text = "${jokeText?.substringBeforeNthDelimiter(" ", 3)} ...",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                // If the joke is marked with any flag, show a warning icon
                if (joke.flags.isAnySet())
                    Icon(
                        imageVector = Icons.TwoTone.Warning,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight()
                    )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

}

/**
 * A composable function that displays a card view for selecting joke query options.
 * It includes options for blocklist, joke type, and number of jokes. It also includes a button
 * to fetch jokes based on the selected options.
 * @param viewModel The ViewModel that provides the joke query options and manages state.
 * @param onGetJokesClicked A callback function that is called when the "Get Jokes" button is clicked.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun JokeQueryCardView(
    viewModel: JokeListViewModel,
    onGetJokesClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // read the options from the viewModel
    val blocklistOptions = viewModel.blocklistOptions.map { it -> stringResource(it.displayName) }
    val jokeTypeOptions = viewModel.jokeTypeOptions.map { it -> stringResource(it.displayName) }
    val numberOfJokesOptions = viewModel.numberOfJokesOptions.map { it.toString() }

    // Observe the query state from the ViewModel
    val queryState by viewModel.jokeQueryUiState.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.blacklist_label),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ChipGroupReflow(
                    blocklistOptions,
                    queryState.blockListSelection,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) { index ->
                    // Handle the selection of blocklist options
                    // TODO: Update the ViewModel with the selected blocklist option
                    //viewModel.updateBlocklistSelection(index)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .alignByBaseline()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        text = stringResource(id = R.string.joke_type_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SingleChoiceSegmentedButton(
                        options = jokeTypeOptions,
                        selectedIndex = queryState.selectedJokeTypeIndex
                    ) {
                        // Handle the joke type selection
                        // TODO: Update the ViewModel with the selected joke type
                        //viewModel.updateJokeType(it)
                    }
                }
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .alignByBaseline()
                            .padding(start = 8.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.number_of_jokes_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenu(
                        options = numberOfJokesOptions,
                        selectedIndex = queryState.selectedNumberOfJokesIndex
                    ) {
                        // Handle the number of jokes selection
                        // TODO: Update the ViewModel with the selected number of jokes
                        //viewModel.updateNumberOfJokes(it)
                    }
                }
            }

            Button(
                enabled = queryState.getJokesButtonEnabled,
                onClick = {
                    // Handle the button click
                    onGetJokesClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Face,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.get_jokes_txt),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * A composable function that displays a dialog with the details of a selected joke.
 */
@Composable
fun JokeDialog(joke: Joke, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.joke_dialog_title_template, joke.category),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )

            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Display the joke text based on its type. The type can be either
                // single or two-part. The single joke text is available in the joke field.
                // The two-part joke text is available in the setup and delivery fields.
                Text(
                    text = (if (joke.type == JokeType.single.name) joke.joke else joke.setup)
                        ?: stringResource(R.string.joke_error),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                if (joke.type == JokeType.twopart.name)
                    Text(
                        text = joke.delivery ?: stringResource(R.string.joke_error),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
            }

        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

