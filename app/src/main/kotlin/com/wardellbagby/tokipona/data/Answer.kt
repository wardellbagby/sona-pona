package com.wardellbagby.tokipona.data

/**
 * A class representing an Answer associated with a [Question].
 * @author Wardell Bagby
 */
data class Answer(val text: String, val isCorrect: Boolean) {
    override fun toString(): String {
        return "Text: \"$text\"; isCorrect=$isCorrect"
    }
}