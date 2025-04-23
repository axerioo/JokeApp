package com.example.jokeapp.ui.screens.categories

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.jokeapp.R
import com.example.jokeapp.model.CategoriesResponse
import com.example.jokeapp.retrofit.RetrofitClient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel class for the CategorySelectScreen. This class is responsible for fetching
 * the categories from the API and updating the UI state.
 */
class CategorySelectViewModel : ViewModel() {
    /** UI state as StateFlow. The _uiState is mutable and private thus can only be modified within this class
     * The uiState is public and can be observed from outside this class. This is a common pattern
     * in Android development to encapsulate the state and provide a read-only interface to it.*/
    private val _uiState = MutableStateFlow<CategoryScreenUiState>(
        CategoryScreenUiState.Loading)
    val uiState: StateFlow<CategoryScreenUiState> = _uiState.asStateFlow()
    /** Get the singleton instance of the ApiService directly*/
    private val jokeApiService = RetrofitClient.apiServiceInstance

    /**
     * Callback object to handle the response from the API when fetching categories. We can create
     * objects on the fly using object keyword in Kotlin. This is useful for creating single-use
     * objects that implement an interface or abstract class. The Callback interface has two methods:
     * onResponse and onFailure. The onResponse method is called when the API call is successful
     * and the onFailure method is called when the API call fails (for example due to network error).
     */
    private val categoriesResponseCallback: Callback<CategoriesResponse> =
        object : Callback<CategoriesResponse> {
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.isSuccessful) {
                    // Update UI state with jokes - the conversion to categories:List<String> is done behind the scenes
                    // by the Moshi converter
                    _uiState.value =
                        CategoryScreenUiState.Success(response.body()?.categories ?: emptyList())
                } else {
                    // Handle API error
                    _uiState.value = CategoryScreenUiState.Error(
                        titleRes = R.string.error,
                        message = "${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                // Handle network error
                _uiState.value = CategoryScreenUiState.Error(
                    titleRes = R.string.network_error,
                    message = "${t.message}"
                )
            }
        }

    /** Function to fetch categories from the API*/
    fun fetchCategories() {
        // Update UI state to Loading - display loading indicator
        _uiState.value = CategoryScreenUiState.Loading
        // Call the API service to get categories with the getCategories() method.
        // Enqueue the callback to handle the response asynchronously
        jokeApiService.getCategories().enqueue(categoriesResponseCallback)
    }

    init {
        fetchCategories()
    }
}

/**
 * Sealed interface to represent the UI state of the CategorySelectScreen.
 * This is a common pattern in Android development to represent different states of
 * the UI in a type-safe way. The sealed interface allows us to define a closed set of
 * subclasses, which makes it easier to handle different states in a when expression.
 * Each subclass represents a different state of the UI, extending the sealed interface. Each
 * subclass can have its own properties and methods, allowing us to encapsulate the
 * state-specific logic within the subclass.
 */
sealed interface CategoryScreenUiState {
    /** Loading state when waiting for API response. When the subclass doesn't have any
     * parameters we use object instead of data class, because data class must have at least
     * one primary constructor parameter.
     * */
    object Loading : CategoryScreenUiState

    /** Success state with categories loaded.
     * @param categories List of categories loaded from the API.
     */
    data class Success(val categories: List<String>) : CategoryScreenUiState

    /** Error state when API call fails or network error occurs.
     * @param titleRes is a string resource ID used to set the title
     * for the error from the strings resources instead of hardcoding it.
     * @param message is the error message to be displayed to the user.
     * */
    data class Error(@StringRes val titleRes: Int, val message: String) : CategoryScreenUiState
}
