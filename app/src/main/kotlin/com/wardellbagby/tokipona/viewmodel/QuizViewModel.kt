package com.wardellbagby.tokipona.viewmodel

import android.os.CountDownTimer
import android.os.Handler
import com.wardellbagby.tokipona.data.Answer
import com.wardellbagby.tokipona.data.DefinitionQuestion
import com.wardellbagby.tokipona.data.GlyphQuestion
import com.wardellbagby.tokipona.data.Question
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.provider.GlyphContentProvider
import com.wardellbagby.tokipona.util.FixedQueue
import com.wardellbagby.tokipona.util.randomItem
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import java.util.Collections
import java.util.Random

/**
 * @author Wardell Bagby
 */
class QuizViewModel(private val words: List<Word>) {

    companion object {
        val MAX_TIMER_VALUE = 15000L
        private val TIMER_INTERVAL = 10L
        private val WAIT_BEFORE_TIMER_START = 1500L
        private val MAX_RECENT_ITEMS_COUNT = 5
    }

    private enum class QuestionType {
        DEFINITION, GLYPH
    }

    private val recentWords: FixedQueue<Word> = FixedQueue(MAX_RECENT_ITEMS_COUNT)
    private var currentQuestion: Question? = null
    private var isQuestionAnswered = false
    private val randomGen = Random()
    private val countdownHandler = Handler()
    private var countdownTimer = QuizCountdownTimer()

    class QuizCountdownTimer : CountDownTimer(MAX_TIMER_VALUE, TIMER_INTERVAL) {
        private val onTickProcessor = BehaviorProcessor.create<Long>()
        private var lastObservedValue = MAX_TIMER_VALUE

        val onTick: Flowable<Long>
            get() = Flowable.just(lastObservedValue).concatWith(onTickProcessor)

        override fun onFinish() {
            onTickProcessor.onComplete()
        }

        override fun onTick(millisUntilFinished: Long) {
            lastObservedValue = millisUntilFinished
            onTickProcessor.onNext(lastObservedValue)
        }
    }

    /**
     *  Returns a [Flowable] that will update with the current time left to answer a [Question].
     */
    fun onTick(): Flowable<Long> {
        return countdownTimer.onTick
    }

    /**
     * Returns the current [Question].
     */
    fun getQuestion(): Question {
        val question = currentQuestion
        if (question != null && !isQuestionAnswered) {
            return question
        }
        isQuestionAnswered = false
        val newQuestion = generateQuestion()
        currentQuestion = newQuestion
        return newQuestion
    }

    /**
     * Answers the current [Question]. Provide a null [answer] or use the no-arg version if the
     * question was answered due to timeout.
     */
    fun answerQuestion(answer: Answer? = null): Boolean {
        isQuestionAnswered = true
        if (currentQuestion == null || answer == null) {
            return false
        }
        resetCountdown()
        return getCorrectAnswer() == answer
    }

    /**
     * Returns the correct [Answer] for the current question.
     */
    fun getCorrectAnswer(): Answer? {
        return currentQuestion?.answers?.first(Answer::isCorrect)
    }

    private fun generateQuestion(): Question {
        resetCountdown()
        startTimer()
        val correctWord = getRandomQuizWord()
        recentWords.add(correctWord)
        val incorrectWords = getRandomUniqueWords(correctWord)
        return when (getQuestionType()) {
            QuizViewModel.QuestionType.DEFINITION -> generateDefinitionQuestion(correctWord, incorrectWords)
            QuizViewModel.QuestionType.GLYPH -> generateGlyphQuestion(correctWord, incorrectWords)
        }
    }

    private fun getRandomQuizWord(): Word {
        var uniqueWord: Word
        do {
            uniqueWord = randomGen.randomItem(words)
        } while (recentWords.contains(uniqueWord))
        return uniqueWord
    }

    private fun getRandomUniqueWords(correctWord: Word, count: Int = 3): List<Word> {
        val uniqueWords = mutableListOf<Word>()
        for (i in 0 until count) {
            var uniqueWord: Word
            do {
                uniqueWord = randomGen.randomItem(words)
            } while (uniqueWords.contains(uniqueWord) || correctWord == uniqueWord)
            uniqueWords.add(uniqueWord)
        }
        return uniqueWords
    }

    private fun resetCountdown() {
        countdownTimer.cancel()
        countdownTimer = QuizCountdownTimer()
    }

    private fun startTimer() {
        @Suppress("ConvertLambdaToReference") //It can't be done. KFunction isn't quite a Runnable.
        countdownHandler.postDelayed({ countdownTimer.start() }, WAIT_BEFORE_TIMER_START)
    }

    private fun generateDefinitionQuestion(correctWord: Word, incorrectWords: List<Word>): Question {
        return DefinitionQuestion(randomGen.randomItem(correctWord.definitions).definitionText, generateAnswers(correctWord, incorrectWords))
    }

    private fun generateGlyphQuestion(correctAnswer: Word, incorrectAnswers: List<Word>): Question {
        return GlyphQuestion(GlyphContentProvider.getUriForWord(correctAnswer), generateAnswers(correctAnswer, incorrectAnswers))
    }

    private fun generateAnswers(correctAnswer: Word, incorrectAnswers: List<Word>): List<Answer> {
        val answers = mutableListOf(Answer(correctAnswer.name, true))
        answers += incorrectAnswers.map { Answer(it.name, false) }
        Collections.shuffle(answers)
        return answers
    }

    private fun getQuestionType(): QuestionType {
        return if (randomGen.nextInt(3) == 2) QuestionType.GLYPH else QuestionType.DEFINITION
    }
}