package com.wardellbagby.tokipona.viewmodel

import com.wardellbagby.tokipona.testutil.getTestWords
import io.reactivex.rxkotlin.blockingSubscribeBy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * @author Wardell Bagby
 */
class QuizViewModelTest {
    private lateinit var model: QuizViewModel

    @Before
    fun setup() {
        model = QuizViewModel(getTestWords())
    }

    @Test
    fun getQuestion() {
        val firstQuestion = model.getQuestion()
        assertEquals(firstQuestion, model.getQuestion())
        assertEquals(firstQuestion, model.getQuestion())
        assertEquals(firstQuestion, model.getQuestion())
        model.answerQuestion()
        val secondQuestion = model.getQuestion()
        assertTrue(firstQuestion != secondQuestion)
        assertEquals(secondQuestion, model.getQuestion())
        assertEquals(secondQuestion, model.getQuestion())
        assertEquals(secondQuestion, model.getQuestion())

    }

    @Test
    fun answerQuestion() {
        //Test correct answer
        val firstQuestion = model.getQuestion()
        val firstQuestionCorrectAnswer = firstQuestion.answers.firstOrNull { it.isCorrect } ?: return fail("Received a question with no correct answer. \"$firstQuestion\"")
        assertTrue(model.answerQuestion(firstQuestionCorrectAnswer))

        //Test incorrect answer
        val secondQuestion = model.getQuestion()
        val secondQuestionIncorrectAnswer = secondQuestion.answers.firstOrNull { !it.isCorrect } ?: return fail("Received a question with no incorrect answer. \"$secondQuestion\"")
        assertFalse(model.answerQuestion(secondQuestionIncorrectAnswer))

        //Test no-args answer.
        model.getQuestion()
        assertFalse(model.answerQuestion())

        //Test answering with an answer from another question.
        model.getQuestion()
        assertFalse(model.answerQuestion(firstQuestionCorrectAnswer))
    }

    @Test
    @Ignore("Can't mock CountdownTimer.") //todo Look into switching CountdownTimer for another class or creating our own.
    fun onTick() {
        model.onTick()
                .timeout(1, TimeUnit.SECONDS)
                .blockingSubscribeBy(onNext = {
                    fail("Received $it when nothing should have been received.")
                }, onError = {})
        model.getQuestion()
        val result = model.onTick()
                .timeout(4, TimeUnit.SECONDS)
                .blockingFirst(-1)
        assertEquals(result, QuizViewModel.MAX_TIMER_VALUE)
    }

}