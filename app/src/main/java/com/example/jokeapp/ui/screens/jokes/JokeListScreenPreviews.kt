package com.example.jokeapp.ui.screens.jokes

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokeapp.R
import com.example.jokeapp.ui.theme.JokeAppTheme


@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8")
@Composable
fun PreviewQueryCardView() {
    JokeAppTheme {
        JokeQueryCardView(
           /*TODO: Provide ViewModel*/,
            onGetJokesClicked = {

            },
            modifier = Modifier.padding(top = 48.dp)
        )
    }
}

val defaultJoke = Joke(
    category = "Programming",
    delivery = "This is a sample joke delivery text.",
    joke = "This is a sample joke text that is quite long and needs to be truncated.",
    error = false,
    flags = Joke.Flags(
        explicit = false,
        nsfw = false,
        political = false,
        racist = false,
        religious = false,
        sexist = false
    ),
    id = 1,
    lang = "en",
    safe = true,
    setup = "This is a sample joke setup text.",
    type = "single"
)
val jokes = listOf(
    defaultJoke,
    defaultJoke.copy(joke = "This is a second joke text that is quite long and needs to be truncated."),
    defaultJoke.copy(joke = "This is a third joke text that is quite long and needs to be truncated.")
)

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun JokeListItemPreview() {
    JokeAppTheme {
        JokeListItem(
            joke = defaultJoke
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun JokeListPreview() {
    JokeAppTheme {
        JokeList(jokes = jokes, modifier = Modifier.padding(top = 48.dp)) {}
    }
}


@Preview(device = "id:pixel_8", showSystemUi = true, showBackground = true)
@Composable
fun JokeListScreenInitialPreview() {
    JokeAppTheme {
        JokeListScreen(
            modifier = Modifier.padding(top = 48.dp),
            /*TODO: Provide ViewModel*/,
        )
    }
}

@Preview(device = "id:pixel_8", showSystemUi = true, showBackground = true)
@Composable
fun JokeListScreenSuccessPreview() {
    /*TODO: setup ViewModel*/
    JokeAppTheme {
        JokeListScreen(
            modifier = Modifier.padding(top = 48.dp),
            /*TODO: Provide ViewModel*/
        )
    }
}

@Preview(device = "id:pixel_8", showSystemUi = true, showBackground = true)
@Composable
fun JokeListScreenErrorPreview() {
    /*TODO: setup ViewModel*/

    JokeAppTheme {
        JokeListScreen(
            modifier = Modifier.padding(top = 48.dp),
            /*TODO: Provide ViewModel*/
        )
    }
}

@Preview(device = "id:pixel_8", showSystemUi = true, showBackground = true)
@Composable
fun JokeListScreenLoadingPreview() {
    /*TODO: setup ViewModel*/

    JokeAppTheme {
        JokeListScreen(
            modifier = Modifier.padding(top = 48.dp),
            /*TODO: Provide ViewModel*/
        )
    }
}

@Preview
@Composable
fun JokeDialogPreview() {
    JokeAppTheme {
        JokeDialog(
            joke = defaultJoke,
            onConfirm = {},
            onDismiss = {}
        )
    }
}