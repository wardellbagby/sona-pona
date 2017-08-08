package com.wardellbagby.tokipona.task

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.data.Word
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

open class LoadWordListTask(val callback: (List<Word>) -> Unit) : BackgroundTask<InputStream, Unit, List<Word>>() {

    override fun onBackgrounded(vararg parameters: InputStream?): List<Word>? {
        val reader = BufferedReader(InputStreamReader(parameters[0]))
        return Gson().fromJson(reader, object : TypeToken<List<Word>>() {}.type)
    }

    override fun onResult(result: List<Word>?) {
        super.onResult(result)
        if (result == null) {
            return
        }
        callback(result.sortedBy(Word::name))
    }
}