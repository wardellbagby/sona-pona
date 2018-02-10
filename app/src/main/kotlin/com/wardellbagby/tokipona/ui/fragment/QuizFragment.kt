package com.wardellbagby.tokipona.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.TokiPonaApplication
import com.wardellbagby.tokipona.data.Answer
import com.wardellbagby.tokipona.data.Question
import com.wardellbagby.tokipona.util.TAG
import com.wardellbagby.tokipona.viewmodel.QuizViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_quiz.*
import javax.inject.Inject

/**
 * @author Wardell Bagby
 */
class QuizFragment : BaseFragment() {

    companion object {
        private const val RESULT_DELAY = 1500L
    }

    @Inject lateinit var quizViewModelProvider: Single<QuizViewModel>
    private var quizViewModel: QuizViewModel? = null
    private val handler = Handler()
    private var correctColor: Int = 0
    private var incorrectColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokiPonaApplication.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_quiz, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setColors(context)
    }

    override fun onResume() {
        super.onResume()
        quizViewModelProvider
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    quizViewModel = it
                    instantiateFromModel()
                }.attach()
    }

    override fun onPause() {
        super.onPause()
        reset()
    }

    @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
    private fun setColors(context: Context) {
        correctColor = context.resources.getColor(R.color.correct)
        incorrectColor = context.resources.getColor(R.color.incorrect)
    }

    private fun animateTransition() {
        TransitionManager.beginDelayedTransition(quiz_question_view, Fade())
        TransitionManager.beginDelayedTransition(quiz_answer_view, AutoTransition())
    }

    private fun reset() {
        quiz_question_view.setQuestion(null)
        quiz_answer_view.removeAllViews()
        quiz_countdown_timer.progressDrawable.setColorFilter(correctColor, PorterDuff.Mode.MULTIPLY)
        handler.removeCallbacksAndMessages(null)
    }

    private fun instantiateFromModel() {
        animateTransition()
        reset()
        val currentQuestion = quizViewModel?.getQuestion() ?: return
        showQuestion(currentQuestion)
        showAnswers(currentQuestion.answers)
        setupTimer()
    }

    private fun showQuestion(question: Question) {
        quiz_question_view.setQuestion(question)
    }

    private fun showAnswers(answers: List<Answer>) {
        val model = quizViewModel ?: return
        answers.forEachIndexed { index, answer ->
            val view = LayoutInflater.from(context).inflate(R.layout.quiz_answer_view, quiz_answer_view, false)
            val answerView: TextView = view.findViewById(R.id.answer_text)
            answerView.text = answer.text
            view.setOnClickListener {
                handleResult(model.answerQuestion(answer), view)
            }
            view.tag = answer
            quiz_answer_view.addView(view, index)
        }
    }

    private fun setupTimer() {
        val model = quizViewModel ?: return
        model.onTick().subscribeWith(OnTickSubscriber()).attach()
    }

    private fun setTimerColor(ratio: Float) {
        val newRatio = (1 - Math.cos(ratio * Math.PI)) / 2
        val red = Math.abs(Color.red(incorrectColor) * (1 - newRatio) + Color.red(correctColor) * newRatio).toInt()
        val green = Math.abs(Color.green(incorrectColor) * (1 - newRatio) + Color.green(correctColor) * newRatio).toInt()
        val blue = Math.abs(Color.blue(incorrectColor) * (1 - newRatio) + Color.blue(correctColor) * newRatio).toInt()
        quiz_countdown_timer.progressDrawable.setColorFilter(Color.rgb(red, green, blue), PorterDuff.Mode.MULTIPLY)
    }

    private fun handleResult(correct: Boolean, selected: View? = null) {
        if (correct) {
            selected?.setBackgroundColor(correctColor)
        } else {
            selected?.setBackgroundColor(incorrectColor)
            val model = quizViewModel ?: return
            val answer = model.getCorrectAnswer()
            (0 until quiz_answer_view.childCount)
                    .map(quiz_answer_view::getChildAt)
                    .first { it.tag == answer }
                    .setBackgroundColor(correctColor)

        }
        handler.postDelayed(this::instantiateFromModel, RESULT_DELAY)
    }

    inner class OnTickSubscriber : ResourceSubscriber<Long>() {
        override fun onStart() {
            super.onStart()
            quiz_countdown_timer.max = QuizViewModel.MAX_TIMER_VALUE.toInt()
            quiz_countdown_timer.progress = quiz_countdown_timer.max
        }

        override fun onComplete() {
            val model = quizViewModel ?: return
            handleResult(model.answerQuestion())
        }

        override fun onError(t: Throwable?) {
            Log.e(TAG, "Something terrible happened in the OnTickSubscriber?", t)
        }

        override fun onNext(progress: Long) {
            //No one man should have all these primitive casts.
            quiz_countdown_timer.progress = progress.toInt()
            setTimerColor((progress / quiz_countdown_timer.max.toFloat()))
        }
    }
}