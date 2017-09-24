package com.wardellbagby.tokipona.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.data.Word
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
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
    fun getWords(context: Context): Single<List<Word>> {
        val words = wordsList
        if (words != null) {
            return Single.just(words)
        }
        val stream = context.assets.open("word_list.json")
        return Single.defer {
            getWordsSync(stream)
        }
    }

    fun getWordsSync(stream: InputStream): Single<List<Word>> {
        val reader = BufferedReader(InputStreamReader(stream))
        val words: List<Word> = Gson().fromJson(reader, object : TypeToken<List<Word>>() {}.type)
        words.sortedBy(Word::name)
        wordsList = words
        return Single.just(words)
    }

    /**
     * Given a [String] of text and a list of [Word]s, glosses the text into a list of [Word]s. The returned
     * list is in the same order as the given text.
     *
     * @param text The text that should be glossed.
     * @param words A word list that contains valid [Word]s. This will be checked against for the glossing functionality.
     */
    private fun gloss(text: String, words: List<Word>): Single<List<Word>> {
        return Single.defer {
            glossSync(words, true, text)
        }

    }

    private fun glossSync(words: List<Word>, includePunctuation: Boolean, text: String): Single<List<Word>> {
        return Single.just(Words.split(text, includePunctuation)?.map { token ->
            words.firstOrNull { it.name == token } ?: Word(token, isValidWord = false)
        } ?: emptyList())
    }

    /**
     * Given a [String] of text and a list of [Word]s, glosses the text into a string of [Word]s.
     *
     * @param text The text that should be glossed.
     * @param words A word list that contains valid [Word]s. This will be checked against for the glossing functionality.
     */
    fun glossToText(text: String, words: List<Word>): Single<String> {
        return gloss(text, words)
                .map(this::reduceToText)
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