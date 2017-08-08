package com.wardellbagby.tokipona.util

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.data.Word
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * @author Wardell Bagby
 */
object Words {

    private val DELIMITERS_ARRAY = " ,;\"?.!':\\()[]{}*^".toCharArray()
    private val REGEX_SAFE_DELIMITERS: String = DELIMITERS_ARRAY.map { "\\" + it }.reduce { left, right -> left + right }
    private val DELIMITERS_PATTERN = Pattern.compile("(?=[$REGEX_SAFE_DELIMITERS])|(?<=[$REGEX_SAFE_DELIMITERS])")

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
    fun gloss(text: String, words: List<Word>, callback: (List<Word>) -> Unit) {
        GlossTask(words, true, callback).execute(text)
    }

    /**
     * Same as [gloss], but returns a [String] to the callback instead of a list of [Word]s.
     */
    fun glossToString(text: String, words: List<Word>, callback: (String) -> Unit) {
        gloss(text, words) {
            if (it.isEmpty()) {
                callback(emptyString())
            }
            val result = it.map(Word::gloss).reduce { total, next ->
                if (Words.isSpecialCharacter(next)) {
                    return@reduce total + next
                }
                return@reduce total + " " + next
            } ?: emptyString()
            callback(result)
        }
    }

    fun isSpecialCharacter(text: String?): Boolean {
        return text != null && text.length == 1 && isSpecialCharacter(text.single())
    }

    fun isSpecialCharacter(character: Char?): Boolean {
        return character != null && character in DELIMITERS_ARRAY
    }

    private class LoadWordListTask(val callback: (List<Word>) -> Unit) : AsyncTask<InputStream, Unit, List<Word>>() {
        override fun doInBackground(vararg p0: InputStream?): List<Word> {
            val reader = BufferedReader(InputStreamReader(p0[0]))
            return Gson().fromJson(reader, object : TypeToken<List<Word>>() {}.type)
        }

        override fun onPostExecute(result: List<Word>) {
            super.onPostExecute(result)
            callback(result.sortedBy(Word::name))
        }
    }

    private class GlossTask(val words: List<Word>, val includePunctuation: Boolean, val callback: (List<Word>) -> Unit) : AsyncTask<String, Unit, List<Word>>() {
        override fun doInBackground(vararg p0: String?): List<Word> {
            val tokens = p0[0]?.split(DELIMITERS_PATTERN)?.filter {
                !it.trim().isBlank() && (includePunctuation || isSpecialCharacter(it))
            }
            return tokens?.map { token ->
                words.firstOrNull { it.name == token } ?: Word(token, isValidWord = false)
            } ?: listOf()
        }

        override fun onPostExecute(result: List<Word>) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}