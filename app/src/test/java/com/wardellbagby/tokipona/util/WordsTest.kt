package com.wardellbagby.tokipona.util

import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.testutil.getTestWords
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

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

    @Test
    fun testGloss_validTokiPona() {
        val tokiPonaString = "esun ijo lili mun jan"
        val expectedResult = "shop thing small moon person"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_invalidTokiPona() {
        val tokiPonaString = "cool dude writes test for fun"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(tokiPonaString, actualResult)
    }

    @Test
    fun testGloss_mixedValidityTokiPona() {
        val tokiPonaString = "lili % dude $$ writes !@ test . for, mun?"
        val expectedResult = "small % dude $$ writes !@ test . for, moon?"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_validTokiPona_withPunctuation() {
        val tokiPonaString = "esun,! ijo** lili' mun jan;, !!"
        val expectedResult = "shop,! thing** small' moon person;, !!"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_invalidTokiPona_withPunctuation() {
        val tokiPonaString = "cool; :dude: [writes] (test) (for) =fun="
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(tokiPonaString, actualResult)
    }

    @Test
    fun testGloss_mixedValidityTokiPona_withPunctuation() {
        val tokiPonaString = "[(lili]) !;dude ; <writes> #test ##for &mun&"
        val expectedResult = "[(small]) !;dude ; <writes> #test ##for &moon&"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), true)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_validTokiPona_withPunctuation_includePunctuationFalse() {
        val tokiPonaString = "esun,! ijo** lili' mun jan;, !!"
        val expectedResult = "shop thing small moon person"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), false)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_invalidTokiPona_withPunctuation_includePunctuationFalse() {
        val tokiPonaString = "cool; :dude: [writes] (test) (for) =fun="
        val expectedResult = "cool dude writes test for fun"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), false)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testGloss_mixedValidityTokiPona_withPunctuation_includePunctuationFalse() {
        val tokiPonaString = "[(lili]) !;dude ; <writes> #test ##for &mun&"
        val expectedResult = "small dude writes test for moon"
        val actualResult = Words.glossToText(tokiPonaString, getTestWords(), false)
                .timeout(2, TimeUnit.SECONDS)
                .blockingGet()
        assertEquals(expectedResult, actualResult)
    }

    private fun convertToWords(vararg words: String): List<Word> = words.map { Word(it) }

}