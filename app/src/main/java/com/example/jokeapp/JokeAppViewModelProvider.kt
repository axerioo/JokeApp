package com.example.jokeapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jokeapp.ui.screens.categories.CategorySelectViewModel
import com.example.jokeapp.ui.screens.jokes.JokeListViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire JokeApp
 */
object JokeAppViewModelProvider {

    val Factory = viewModelFactory {
        // Initializer for CategorySelectViewModel
        initializer {
            CategorySelectViewModel()
        }

        // Initializer for JokeListViewModel
        initializer {
            JokeListViewModel(
                this.createSavedStateHandle()
            )
        }
    }

    val PreviewFactory = viewModelFactory {
        // Initializer for CategorySelectViewModel
        initializer {
            CategorySelectViewModel()
        }

        // Initializer for JokeListViewModel
        initializer {
            JokeListViewModel(
                SavedStateHandle(
                    mapOf("category" to "Programming")
                )
            )
        }
    }
}