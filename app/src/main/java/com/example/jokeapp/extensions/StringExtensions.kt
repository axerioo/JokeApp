package com.example.jokeapp.extensions

/**
 * Extension function to get the substring of a string before the nth occurrence of a delimiter.
 * @param delimiter The delimiter to search for in the string.
 * @param n The occurrence number of the delimiter to search for.
 * @return The substring of the string before the nth occurrence of the delimiter.
 */
fun String.substringBeforeNthDelimiter(delimiter: String, n: Int): String {
    var index = -1
    // Repeat the block of code n times
    repeat(n) {
        // Find the next occurrence of the delimiter (after the last found index)
        index = this.indexOf(delimiter, index + 1)
        // If the delimiter is not found (indexOf returns -1), return the whole string
        if (index == -1) return this
    }
    // Return the substring before the nth occurrence of the delimiter
    return this.substring(0, index)
}