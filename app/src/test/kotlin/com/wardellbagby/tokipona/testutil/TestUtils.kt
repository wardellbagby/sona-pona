package com.wardellbagby.tokipona.testutil

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.data.Word

/**
 * @author Wardell Bagby
 */
fun getTestWords(): List<Word> {
    return Gson().fromJson(testWords, object : TypeToken<List<Word>>() {}.type)
}

private val testWords = "[{\"name\":\"esun\",\"definitions\":[{\"part_of_speech\":\"NOUN\",\"definition\":\"market, shop\"}],\"gloss\":\"shop\"},{\"name\":\"ijo\",\"definitions\":[{\"part_of_speech\":\"NOUN\",\"definition\":\"thing, something, stuff, anything, object\"},{\"part_of_speech\":\"MODIFIER\",\"definition\":\"of something\"},{\"part_of_speech\":\"TRANSITIVE_VERB\",\"definition\":\"objectify\"}],\"gloss\":\"thing\"},{\"name\":\"jan\",\"definitions\":[{\"part_of_speech\":\"NOUN\",\"definition\":\"person, people, human, being, somebody, anybody\"},{\"part_of_speech\":\"MODIFIER\",\"definition\":\"human, somebody's, personal, of people\"},{\"part_of_speech\":\"TRANSITIVE_VERB\",\"definition\":\"personify, humanize, personalize\"}],\"gloss\":\"person\"},{\"name\":\"lili\",\"definitions\":[{\"part_of_speech\":\"MODIFIER\",\"definition\":\"small, little, young, a bit, short, few, less\"},{\"part_of_speech\":\"TRANSITIVE_VERB\",\"definition\":\"reduce, shorten, shrink, lessen\"}],\"gloss\":\"small\"},{\"name\":\"mun\",\"definitions\":[{\"part_of_speech\":\"NOUN\",\"definition\":\"moon\"},{\"part_of_speech\":\"MODIFIER\",\"definition\":\"lunar\"}],\"gloss\":\"moon\"}]"