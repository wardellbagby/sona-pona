package com.wardellbagby.tokipona.util

import com.wardellbagby.tokipona.data.Word
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Wardell Bagby
 */
class WordsTest {

    @Test
    fun testReduceToText_noPunctuation() {
        val expectedResult = "mi olin e sina"
        val argument = convertToWords("mi", " ", "olin", " ", "e", " ", "sina")
        val actualResult = Words.reduceToText(argument)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testReduceToText_withPunctuation() {
        val expectedResult = "tenpo ni la, mi olin e sina! You're cool!"
        val argument = convertToWords("tenpo", " ", "ni", " ", "la", ",", " ", "mi", " ", "olin", " ", "e", " ", "sina", "!", " ", "You", "'", "re", " ", "cool", "!")
        val actualResult = Words.reduceToText(argument)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testReduceToText_oddPunctuation() {
        val expectedResult = "ten' /po"
        val argument = convertToWords("ten", "'", " ", "/", "po")
        val actualResult = Words.reduceToText(argument)
        assertEquals(expectedResult, actualResult)
    }

    private fun convertToWords(vararg words: String): List<Word> = words.map { Word(it) }

}