package com.example.jokeapp.retrofit


import androidx.annotation.StringRes
import com.example.jokeapp.R
import com.example.jokeapp.model.CategoriesResponse
import com.example.jokeapp.model.JokesResponse
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The Blocklist enum class represents the different blocklist options available for filtering jokes.
 * The entries names in this enum are used to specify which types of jokes should be blocked.
 * To match the API calls the enum names are in lowercase.
 * Each option is associated with a string resource ID for display purposes.
 * The @StringRes annotation indicates that the value is a reference to a string resource.
 */
enum class Blocklist(@StringRes val displayName: Int) {
    nsfw(R.string.nsfw_block),
    religious(R.string.religious_block),
    political(R.string.political_block),
    racist(R.string.racist_block),
    sexist(R.string.sexist_block),
    explicit(R.string.explicit_block)
}

/**
 * The JokeType enum class represents the different joke types available.
 * The API supports two types of jokes: single and two-part jokes.
 * The entries names in this enum are used to specify the type of joke.
 * Each joke type has a different structure in the API response.
 */
enum class JokeType(@StringRes val displayName: Int) {
    single(R.string.single_joke_type),
    twopart(R.string.twopart_joke_type)
}

/** The RetrofitClient object is a singleton class that creates a
 * service instance to access the API
 */
object RetrofitClient {
    // Base URL for the API
    private const val BASE_URL = "https://v2.jokeapi.dev/"

    // Function to build and return a Retrofit instance
    private fun getClient(): Retrofit {
        // Create a Moshi instance for JSON parsing KotlinJsonAdapterFactory is used to
        // automatically generate the adapter for the data classes
        val moshi = Moshi.Builder().build()
        // Create a Retrofit instance with the BASE_URL and Moshi converter
        // using the builder pattern
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        // Return the Retrofit instance
        return retrofit
    }
    /**
     * The apiServiceInstance property is a lazy-initialized instance of the [ApiService] interface.
     */
    val apiServiceInstance: ApiService by lazy {
        // Create a Retrofit instance using the getClient() method to access the API
        // via the ApiService interface
        getClient().create(ApiService::class.java)
    }
}

/**
 * The ApiService interface defines the endpoints for the API.
 */
interface ApiService {

    /**
     * GET request to fetch the Categories from the API.
     * [Call] is used to make the network request asynchronously and return the response in a callback.
     *
     * @return [Call] object with [CategoriesResponse]
     */
    @GET("categories")
    fun getCategories(): Call<CategoriesResponse>

    /**
     * GET request to fetch the Jokes from the API based on the category, amount, blacklist flags, and type.
     * Category is part of the URL path (@Path annotation), amount is a query (@Query) parameter,
     * blacklist flags and type are optional query parameters.
     * @Query parameters are used to pass key-value pairs in the URL query string (e.g. ?amount=value1&type=value2).
     * The response is a list of Jokes wrapped in a JokesResponse object retrieved asynchronously with a [Call].
     *
     * @param category The category of jokes to fetch.
     * @param amount The number of jokes to fetch.
     * @param blacklistFlags Optional blacklist flags to filter jokes.
     * @param type Optional type of jokes to fetch.
     * @return [Call] object with [JokesResponse]
     */
    @GET("joke/{category}")
    fun getJokes(
        @Path("category") category: String,
        @Query("amount") amount: Int,
        @Query("blacklistFlags") blacklistFlags: String? = null,
        @Query("type") type: String? = null
    ): Call<JokesResponse>
}
