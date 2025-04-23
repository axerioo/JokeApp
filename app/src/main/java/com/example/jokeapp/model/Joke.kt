package com.example.jokeapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class for the joke containing the category, delivery, joke text, error status,
 * flags, id, language, safe status, setup and type of the joke.
 * The safe call operator "?" is used to handle null values in the response where the field is not present e.g.
 * setup and delivery are only present in two-part jokes.
 */
@JsonClass(generateAdapter = true) // Moshi annotation to generate the JSON adapter for the Joke class
data class Joke(
    @Json(name = "category")
    val category: String,
    @Json(name = "delivery")
    val delivery: String?,
    @Json(name = "joke")
    val joke: String?,
    @Json(name = "error")
    val error: Boolean?,
    @Json(name = "flags")
    val flags: Flags,
    @Json(name = "id")
    val id: Int,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "safe")
    val safe: Boolean,
    @Json(name = "setup")
    val setup: String?,
    @Json(name = "type")
    val type: String
) {
    @JsonClass(generateAdapter = true)
    data class Flags(
        @Json(name = "explicit")
        val explicit: Boolean,
        @Json(name = "nsfw")
        val nsfw: Boolean,
        @Json(name = "political")
        val political: Boolean,
        @Json(name = "racist")
        val racist: Boolean,
        @Json(name = "religious")
        val religious: Boolean,
        @Json(name = "sexist")
        val sexist: Boolean
    ) {}
}

data class Flags(
    @Json(name = "explicit")
    val explicit: Boolean,
    @Json(name = "nsfw")
    val nsfw: Boolean,
    @Json(name = "political")
    val political: Boolean,
    @Json(name = "racist")
    val racist: Boolean,
    @Json(name = "religious")
    val religious: Boolean,
    @Json(name = "sexist")
    val sexist: Boolean
) {
    /** function to check if any of the flags are set to true */
    fun isAnySet(): Boolean {
        return (explicit || nsfw || political || racist || religious || sexist)
    }
}
