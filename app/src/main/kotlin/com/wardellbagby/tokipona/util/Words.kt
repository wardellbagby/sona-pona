package com.wardellbagby.tokipona.util

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.data.PartsOfSentence
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
     * @param includeSpecialCharacters Whether or not special characters should be returned in the result or omitted.
     * @param callback The callback that will be invoked with the result of this transaction.
     */
    fun gloss(text: String, words: List<Word>, includeSpecialCharacters: Boolean = false, callback: (List<Word>) -> Unit) {
        GlossTask(words, includeSpecialCharacters, callback).execute(text)
    }

    /**
     * Same as [gloss], but returns a [String] to the callback instead of a list of [Word]s.
     */
    fun glossToString(text: String, words: List<Word>, includeSpecialCharacters: Boolean = false, callback: (String) -> Unit) {
        gloss(text, words, includeSpecialCharacters) {
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

    @Suppress("unused", "UNUSED_PARAMETER") //Will be used in the future. (Date written: 8/2/2017, remove by 2/2/2018)
    fun tokenize(sentence: String, words: List<Word>): List<Pair<PartsOfSentence, String>> {
        val tokens = sentence.split(DELIMITERS_PATTERN).filter { it != " " }
        val partsOfSentence: MutableList<Pair<PartsOfSentence, String>> = mutableListOf()
        var index = 0
        var start = 0
        if (tokens.firstOrNull { it == "sina" || it == "mi" } != null) {
            partsOfSentence.addAll(handleSubject(tokens.subList(0, 1)))
            partsOfSentence.addAll(handlePredicate(tokens.subList(1, tokens.size)))
            return partsOfSentence
        }
        while (index < tokens.size) {
            val currentWord = tokens[index]
            when (currentWord) {
                "o" -> {
                    partsOfSentence.addAll(handleVocative(tokens.subList(start, index)))
                    start = index
                }
                "li" -> {
                    partsOfSentence.addAll(handleSubject(tokens.subList(start, index)))
                    partsOfSentence.addAll(handlePredicate(tokens.subList(index + 1, tokens.size)))
                    start = index
                }
            }
            index++
        }
        return partsOfSentence
    }

    private fun handleSubject(tokens: List<String>): List<Pair<PartsOfSentence, String>> {
        val subjectParts = mutableListOf<Pair<PartsOfSentence, String>>()
        subjectParts.add(Pair(PartsOfSentence.SUBJECT, tokens[0]))
        if (tokens.size > 1) {
            tokens.subList(1, tokens.size).forEach { subjectParts.add(Pair(PartsOfSentence.MODIFIER, it)) }
        }
        return subjectParts
    }

    private fun handleVocative(tokens: List<String>): List<Pair<PartsOfSentence, String>> {
        val vocativeParts = mutableListOf<Pair<PartsOfSentence, String>>()
        for (token in tokens) {

        }
        return vocativeParts
    }

    //TODO This can probably be combined with handleDirectObject since the logic is almost the exact same.
    private fun handlePredicate(tokens: List<String>, isConjunction: Boolean = false): List<Pair<PartsOfSentence, String>> {
        val predicateParts = mutableListOf<Pair<PartsOfSentence, String>>()
        var index = 0
        if (!isConjunction) {
            predicateParts.add(Pair(PartsOfSentence.VERB, tokens[0]))
            index = 1
        }
        loop@ while (index < tokens.size) {
            val token = tokens[index]
            when (token) {
                "e" -> {
                    if (index + 1 > tokens.size - 1) {
                        predicateParts.addAll(handleDirectObject(tokens.subList(index + 1, tokens.size)))
                    } else {
                        predicateParts.add(Pair(PartsOfSentence.ERROR, "\"e\" should be followed by a direct object."))
                    }
                    break@loop
                }
                "li" -> {
                    predicateParts.add(Pair(PartsOfSentence.CONJUNCTION, "li"))
                    if (index + 1 < tokens.size) {
                        predicateParts.addAll(handlePredicate(tokens.subList(index + 1, tokens.size), isConjunction = true))
                        return predicateParts
                    } else {
                        predicateParts.add(Pair(PartsOfSentence.ERROR, "\"li\" should be followed by a verb."))
                    }
                }
                else -> predicateParts.add(Pair(PartsOfSentence.MODIFIER, token))
            }
            index++
        }
        return predicateParts
    }

    private fun handleDirectObject(tokens: List<String>, isConjunction: Boolean = false): List<Pair<PartsOfSentence, String>> {
        val directObjectParts = mutableListOf<Pair<PartsOfSentence, String>>()
        var index = 0
        if (!isConjunction) {
            directObjectParts.add(Pair(PartsOfSentence.DIRECT_OBJECT, tokens[0]))
            index = 1
        }
        while (index < tokens.size) {
            val token = tokens[index]
            if (token == "e") {
                directObjectParts.add(Pair(PartsOfSentence.CONJUNCTION, "e"))
                if (index + 1 < tokens.size) {
                    directObjectParts.addAll(handleDirectObject(tokens.subList(index + 1, tokens.size), isConjunction = true))
                    return directObjectParts
                } else {
                    directObjectParts.add(Pair(PartsOfSentence.ERROR, "\"e\" should be followed by a direct object."))
                }
            } else {
                directObjectParts.add(Pair(PartsOfSentence.MODIFIER, token))
            }
            index++
        }
        return directObjectParts
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