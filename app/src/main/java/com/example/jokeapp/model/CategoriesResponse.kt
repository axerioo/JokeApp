package com.example.jokeapp.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Data class for the response from the API containing the list of joke categories and their aliases
 * and other data like error status and timestamp
 */
@JsonClass(generateAdapter = true)
data class CategoriesResponse(
    @Json(name = "categories")
    val categories: List<String>,
    @Json(name = "categoryAliases")
    val categoryAliases: List<CategoryAliase>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "timestamp")
    val timestamp: Long
) {
    /** Data class for the category aliases containing the alias and the resolved category name*/
    @JsonClass(generateAdapter = true)
    data class CategoryAliase(
        @Json(name = "alias")
        val alias: String,
        @Json(name = "resolved")
        val resolved: String
    )
}
