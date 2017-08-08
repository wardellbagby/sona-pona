package com.wardellbagby.tokipona.task

import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.testutil.LambdaListener
import com.wardellbagby.tokipona.testutil.getTestWords
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

/**
 * @author Wardell Bagby
 */
class GlossTaskTest {

    @Test
    fun testGloss_validTokiPona() {
        val words = getTestWords()
        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "esun ijo lili mun jan"
        val expectedResult = "shop thing small moon person"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_invalidTokiPona() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "cool dude writes test for fun"
        val expectedResult = tokiPonaString
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_mixedValidityTokiPona() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "lili % dude $$ writes !@ test . for, mun?"
        val expectedResult = "small % dude $$ writes !@ test . for, moon?"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_validTokiPona_withPunctuation() {
        val words = getTestWords()
        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "esun,! ijo** lili' mun jan;, !!"
        val expectedResult = "shop,! thing** small' moon person;, !!"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_invalidTokiPona_withPunctuation() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "cool; :dude: [writes] (test) (for) =fun="
        val expectedResult = tokiPonaString
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_mixedValidityTokiPona_withPunctuation() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "[(lili]) !;dude ; <writes> #test ##for &mun&"
        val expectedResult = "[(small]) !;dude ; <writes> #test ##for &moon&"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, true, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_validTokiPona_withPunctuation_includePunctuationFalse() {
        val words = getTestWords()
        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "esun,! ijo** lili' mun jan;, !!"
        val expectedResult = "shopthingsmallmoonperson"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, false, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_invalidTokiPona_withPunctuation_includePunctuationFalse() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "cool; :dude: [writes] (test) (for) =fun="
        val expectedResult = "cooldudewritestestforfun"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, false, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    @Test
    fun testGloss_mixedValidityTokiPona_withPunctuation_includePunctuationFalse() {
        val words = getTestWords()

        val lambdaListener = Mockito.mock(LambdaListener<List<Word>?>()::class.java)
        val tokiPonaString = "[(lili]) !;dude ; <writes> #test ##for &mun&"
        val expectedResult = "smalldudewritestestformoon"
        val callback = { result: List<Word> ->
            lambdaListener.invoked(result)
            assertEquals(expectedResult, convertResultToString(result))
        }
        GlossTask(words, false, callback).apply {
            onResult(onBackgrounded(tokiPonaString))
        }
        Mockito.verify(lambdaListener).invoked(Mockito.any())
    }

    private fun convertResultToString(words: List<Word>?): String? {
        return words?.map(Word::gloss)?.reduce { total, next ->
            total + next
        }
    }
}