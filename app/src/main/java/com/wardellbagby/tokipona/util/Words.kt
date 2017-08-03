package com.wardellbagby.tokipona.util

import android.content.Context
import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.model.PartsOfSentence
import com.wardellbagby.tokipona.model.Word
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * @author Wardell Bagby
 */
object Words {

    const val DELIMITERS: String = " ,;\"?.!':()[]{}*^"

    private var wordsList: List<Word>? = null

    /**
     * Initializes and returns the list of {@link Word}
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

    @Suppress("unused", "UNUSED_PARAMETER") //Will be used in the future. (Date written: 8/2/2017, remove by 2/2/2018)
    fun tokenize(sentence: String, words: List<Word>): List<Pair<PartsOfSentence, String>> {
        val tokens = sentence.split(Pattern.compile("(?=[$DELIMITERS])|(?<=[$DELIMITERS])")).filter { it != " " }
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
                "o"  -> {
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
                "e"  -> {
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
            callback(result)
        }
    }
}