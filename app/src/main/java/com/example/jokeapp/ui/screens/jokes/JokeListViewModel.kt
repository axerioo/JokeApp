package com.example.jokeapp.ui.screens.jokes

import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.jokeapp.R
import com.example.jokeapp.model.Joke
import com.example.jokeapp.model.JokesResponse
import com.example.jokeapp.retrofit.Blocklist
import com.example.jokeapp.retrofit.JokeType
import com.example.jokeapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel class for the JokeListScreen. This class is responsible for fetching
 * the jokes from the API and updating the UI state. The UI have two parts:
 * 1. The list of jokes
 * 2. The query parameters for fetching the jokes
 * They are represented by the JokeListUiState and JokeQueryUiState classes respectively.
 */
class JokeListViewModel(
    /**
     * The SavedStateHandle is used to save and restore the state of the category parameter
     * when the configuration changes (e.g. screen rotation).
     */
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /** Get the singleton instance of the ApiService directly*/
    private val jokeApiService = RetrofitClient.apiServiceInstance

    // Predefined options for the number of jokes, blocklist and joke type
    // for the query parameters and the UI state.
    val numberOfJokesOptions = (2..10 step 2).toList()
    val blocklistOptions = Blocklist.entries
    val jokeTypeOptions = JokeType.entries

    private val _uiState = MutableStateFlow<JokeListUiState>(JokeListUiState.Initial)

    /** UI state as StateFlow similar to the [CategorySelectViewModel].
     * It has four states: Initial, Loading, Error and Success.
     * */
    val uiState: StateFlow<JokeListUiState> = _uiState.asStateFlow()

    private val _jokeQueryUiState = MutableStateFlow(JokeQueryUiState())

    /** Query parameters state variable. Used to hold the state of the
     * query parameters for fetching the jokes.*/
    val jokeQueryUiState: StateFlow<JokeQueryUiState> = _jokeQueryUiState.asStateFlow()

    /** The query parameters to be used for fetching the jokes from the API.
     * Initialized with the category passed from the savedStateHandle.
     */
    private var queryParams =
        JokeQueryParameters(
            category = savedStateHandle["category"] ?: "",
            numberOfJokes = numberOfJokesOptions.first(),
            blocklist = emptyList(),
            jokeType = jokeTypeOptions.first()
        )

    private val _selectedJoke = MutableStateFlow<Joke?>(null)

    /**
     * The selected joke to be shown in the details dialog.
     * This is a nullable value that is updated when the user selects
     * a joke from the list. Change in this value will trigger the
     * recomposition of the UI that observes it.
     */
    val selectedJoke: StateFlow<Joke?> = _selectedJoke.asStateFlow()

    private val jokesResponseCallback: Callback<JokesResponse> = object : Callback<JokesResponse> {
        override fun onResponse(
            call: Call<JokesResponse>,
            response: Response<JokesResponse>
        ) {
            if (response.isSuccessful) {
                // Update UI state with jokes
                _uiState.value = JokeListUiState.Success(response.body()?.jokes ?: emptyList())
            } else {
                // Handle API error
                _uiState.value = JokeListUiState.Error(
                    titleRes = R.string.error,
                    message = "${response.message()}"
                )
            }
            updateButtonState(true)
        }

        override fun onFailure(call: Call<JokesResponse>, t: Throwable) {
            // Handle network error
            _uiState.value =
                JokeListUiState.Error(titleRes = R.string.network_error, message = "${t.message}")
            updateButtonState(true)
        }
    }

    /**
     * Update the selected blocklist indices
     * @param index The index of the selected blocklist used to update the query parameters
     * and the UI state.
     */
    fun updateBlocklistSelection(index: Int) {
        _jokeQueryUiState.update { currentParams ->
            // Toggle the selection state of the blocklist at the given index.
            // The selectedBlocklistIndices is a immutable list of booleans so we need to create
            // a mutable copy of it to update the value at the given index.
            val newBlocklistSelection = currentParams.blockListSelection.toMutableList()
            newBlocklistSelection[index] = !newBlocklistSelection[index]

            // Update the actual blocklist values based on the selected indices. mapIndexedNotNull is
            // used to iterate over the indices and values of the selectedBlocklistIndices list.
            // If the value at the given index is selected (i.e. true), we add the corresponding
            // Blocklist entry to the new list.
            val selectedBlocklist = newBlocklistSelection.mapIndexedNotNull { i, selected ->
                if (selected) Blocklist.entries[i] else null
            }
            // Update the query parameters with the selected blocklist
            queryParams = queryParams.copy(
                blocklist = selectedBlocklist
            )
            // Update the UI state with the new selected blocklist indices
            currentParams.copy(
                blockListSelection = newBlocklistSelection,
            )
        }
    }

    /** Update the selected joke type
     * @param index The index of the selected joke type used to update the query parameters
     * and the UI state.
     * */
    fun updateJokeType(index: Int) {
        // Update query parameters with the selected joke type
        _jokeQueryUiState.update { currentParams ->
            queryParams = queryParams.copy(jokeType = JokeType.entries[index])
            currentParams.copy(selectedJokeTypeIndex = index)
        }
    }

    /** Update the selected number of jokes
     * @param index The index of the selected number of jokes used to update the query parameters
     * and the UI state.
     * */
    fun updateNumberOfJokes(index: Int) {
        // Update query parameters with the selected number of jokes
        _jokeQueryUiState.update { currentParams ->
            queryParams = queryParams.copy(numberOfJokes = numberOfJokesOptions[index])
            currentParams.copy(selectedNumberOfJokesIndex = index)
        }
    }

    /** Fetch jokes with current query parameters*/
    fun fetchJokes() {

        // Update UI state to Loading
        _uiState.value = JokeListUiState.Loading
        updateButtonState(false)

        // Convert blocklist to format required by API
        val blocklistParam = queryParams.blocklist.joinToString(",")
        // Create jokes call with the current query parameters with
        // the getJokes() method of ApiService
        val jokesCall = jokeApiService.getJokes(
            category = queryParams.category,
            type = queryParams.jokeType.name,
            amount = queryParams.numberOfJokes,
            blacklistFlags = blocklistParam
        )
        // Enqueue the callback to handle the response asynchronously
        jokesCall.enqueue(jokesResponseCallback)
    }

    /**
     * Update the button state
     */
    fun updateButtonState(enabled: Boolean) {
        // Update the button state
        _jokeQueryUiState.update { currentParams ->
            currentParams.copy(getJokesButtonEnabled = enabled)
        }
    }

    /**
     * Select a joke. This is used to show the joke details dialog when the user
     * clicks on a joke in the list.
     */
    fun selectJoke(joke: Joke) {
        _selectedJoke.value = joke
    }

    /**
     * Clear the selected joke. This is used to reset the selected joke when the user
     * closes the joke details dialog.
     */
    fun clearSelectedJoke() {
        _selectedJoke.value = null
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setPreviewUiState(state: JokeListUiState) {
        _uiState.value = state
    }
}

/** Sealed interface for the UI state of JokeListScreen*/
sealed interface JokeListUiState {
    /** Initial state when the screen is first loaded*/
    object Initial : JokeListUiState

    /** Loading state when waiting for API response*/
    object Loading : JokeListUiState

    /** Success state with jokes loaded*/
    data class Success(val jokes: List<Joke>) : JokeListUiState

    /** Error state when API call fails*/
    data class Error(@StringRes val titleRes: Int, val message: String) : JokeListUiState
}

/**
 * Data class to hold the UI state of the query parameters
 * @param getJokesButtonEnabled The state of the get jokes button.
 * @param blockListSelection The state of selected blocklist indices.
 * @param selectedJokeTypeIndex The state of the selected joke type.
 * @param selectedNumberOfJokesIndex The state of the selected number of jokes.
 */
data class JokeQueryUiState(
    val getJokesButtonEnabled: Boolean = true,
    // List of booleans to hold the state of selected blocklist indices
    // it uses BlockList.entries.size to set the correct size
    val blockListSelection: List<Boolean> = List(Blocklist.entries.size) { false },
    val selectedJokeTypeIndex: Int = 0,
    val selectedNumberOfJokesIndex: Int = 0,
)
