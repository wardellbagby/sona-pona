package com.wardellbagby.tokipona.task

import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.util.Words

class GlossTask(private val words: List<Word>, private val includePunctuation: Boolean, private val callback: (List<Word>) -> Unit) : BackgroundTask<String, Unit, List<Word>>() {

    override fun onBackgrounded(vararg parameters: String?): List<Word>? {
        if (parameters.isEmpty()) {
            return emptyList()
        }
        return Words.split(parameters.single(), includePunctuation)?.map { token ->
            words.firstOrNull { it.name == token } ?: Word(token, isValidWord = false)
        } ?: emptyList()
    }

    override fun onResult(result: List<Word>?) {
        callback(result ?: emptyList())
    }
}