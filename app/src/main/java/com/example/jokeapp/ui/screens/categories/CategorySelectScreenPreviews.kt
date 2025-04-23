package com.example.jokeapp.ui.screens.categories

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokeapp.ui.theme.JokeAppTheme

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CategorySelectScreenPreview() {
    JokeAppTheme {
        CategorySelectScreen(
            modifier = Modifier.padding(top = 48.dp),
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    JokeAppTheme {
        CategoryItem(
            categoryName = "Programming",
            onCategoryClick = {}
        )
    }
}