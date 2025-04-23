package com.example.jokeapp.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class for the response from the API containing the list of jokes
 * and other data like error status and timestamp. This is a concatenation
 * of two types of responses from the API: a normal one where error is false
 * and jokes are present, and an error response where error is true. The safe
 * call operator "?" is used to handle null values in the response where the
 * field is not present.
 */
@JsonClass(generateAdapter = true)
data class JokesResponse(
    @Json(name = "amount")
    val amount: Int?, // valid for success responses only
    @Json(name = "error")
    val error: Boolean, // valid for both error and success responses
    @Json(name = "jokes")
    val jokes: List<Joke>?, // valid for success responses only
    @Json(name = "additionalInfo")
    val additionalInfo: String?, // valid for error responses only
    @Json(name = "causedBy")
    val causedBy: List<String>?, // valid for error responses only
    @Json(name = "code")
    val code: Int?, // valid for error responses only
    @Json(name = "internalError")
    val internalError: Boolean?, // valid for error responses only
    @Json(name = "message")
    val message: String?, // valid for error responses only
    @Json(name = "timestamp")
    val timestamp: Long? // valid for both error and success responses
)
