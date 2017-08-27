package com.wardellbagby.tokipona.util

import android.content.Context
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.task.GlossTask
import com.wardellbagby.tokipona.task.LoadWordListTask
import java.util.regex.Pattern

/**
 * Logic useful for dealing with [Word]s.
 * @author Wardell Bagby
 */
object Words {

    private val REGEX_EXCLUDE_DELIMITERS = Pattern.compile("[\\W]")
    private val REGEX_KEEP_DELIMITERS = Pattern.compile("(?=[\\W])|(?<=[\\W])")

    private var wordsList: List<Word>? = null

    /**
     * Initializes and returns a list of Toki Pona [Word]s. Future calls after the initial call
     * will return the cached copy.
     */
    fun getWords(context: Context, callback: (List<Word>) -> Unit) {
        if (wordsList != null) {
            callback(wordsList ?: listOf())
            return
        }
        val stream = context.assets.open("word_list.json")
        LoadWordListTask {
            wordsList = it
            callback(it)
        }.execute(stream)
    }

    /**
     * Given a [String] of text and a list of [Word]s, glosses the text into a list of [Word]s. The returned
     * list is in the same order as the given text.
     *
     * @param text The text that should be glossed.
     * @param words A word list that contains valid [Word]s. This will be checked against for the glossing functionality.
     * @param callback The callback that will be invoked with the result of this transaction.
     */
    private fun gloss(text: String, words: List<Word>, callback: (List<Word>) -> Unit) {
        GlossTask(words, true, callback).execute(text)
    }

    /**
     * Convenience method for calling [gloss] and passing the result to [reduceToText]
     *
     */
    fun glossToText(text: String, words: List<Word>, callback: (String) -> Unit) {
        gloss(text, words) {
            callback(reduceToText(it))
        }
    }

    /**
     * Reduces a list of [Word] into human readable text with spacing and punctuation,
     * if punctuation is in the provided [glossedWords] list.
     *
     */
    fun reduceToText(glossedWords: List<Word>): String {
        if (glossedWords.isEmpty()) {
            return emptyString()
        }
        return glossedWords.map(Word::gloss).reduce { total, next ->
            if (Words.isSpecialCharacter(next) || Words.isSpecialCharacter(total?.last())) {
                return@reduce total + next
            }
            return@reduce total + " " + next
        } ?: emptyString()
    }

    private fun isSpecialCharacter(text: String?): Boolean {
        return text != null && text.length == 1 && isSpecialCharacter(text.single())
    }

    private fun isSpecialCharacter(char: Char?): Boolean {
        return char != null && !Character.isLetter(char)
    }

    fun split(text: String?, includePunctuation: Boolean = true): List<String>? {
        return text?.split(if (includePunctuation) REGEX_KEEP_DELIMITERS else REGEX_EXCLUDE_DELIMITERS)
    }

}