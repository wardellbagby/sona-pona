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
import com.bumptech.glide.Glide
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.TokiPonaApplication
import com.wardellbagby.tokipona.data.Answer
import com.wardellbagby.tokipona.data.DefinitionQuestion
import com.wardellbagby.tokipona.data.GlyphQuestion
import com.wardellbagby.tokipona.data.Question
import com.wardellbagby.tokipona.util.TAG
import com.wardellbagby.tokipona.viewmodel.QuizViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.quiz_definition_view.view.*
import kotlinx.android.synthetic.main.quiz_glyph_view.view.*
import javax.inject.Inject

/**
 * @author Wardell Bagby
 */
class QuizFragment : BaseFragment() {

    companion object {
        private val RESULT_DELAY = 1500L
    }

    @Inject lateinit var mQuizViewModelProvider: Single<QuizViewModel>
    private var mQuizViewModel: QuizViewModel? = null
    private val mHandler = Handler()
    private var mOnTickDisposable: Disposable? = null
    private var mCorrectColor: Int = 0
    private var mIncorrectColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokiPonaApplication.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setColors(context)
    }

    override fun onResume() {
        super.onResume()
        mQuizViewModelProvider
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    mQuizViewModel = it
                    instantiateFromModel()
                }
    }

    override fun onPause() {
        super.onPause()
        reset()
    }

    @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
    private fun setColors(context: Context) {
        mCorrectColor = context.resources.getColor(R.color.correct)
        mIncorrectColor = context.resources.getColor(R.color.incorrect)
    }

    private fun animateTransition() {
        TransitionManager.beginDelayedTransition(quiz_question_container_view, Fade())
        TransitionManager.beginDelayedTransition(quiz_answer_view, AutoTransition())
    }

    private fun reset() {
        if (mOnTickDisposable?.isDisposed == false) {
            mOnTickDisposable?.dispose()
        }
        quiz_question_container_view.removeAllViews()
        quiz_answer_view.removeAllViews()
        quiz_countdown_timer.progressDrawable.setColorFilter(mCorrectColor, PorterDuff.Mode.MULTIPLY)
    }

    private fun instantiateFromModel() {
        animateTransition()
        reset()
        val currentQuestion = mQuizViewModel?.getQuestion() ?: return
        showQuestion(currentQuestion)
        showAnswers(currentQuestion.answers)
        setupTimer()
    }

    private fun showQuestion(question: Question) {
        when (question) {
            is DefinitionQuestion -> showDefinitionQuestion(question)
            is GlyphQuestion -> showGlyphQuestion(question)
        }
    }

    private fun showDefinitionQuestion(question: DefinitionQuestion) {
        val definitionView = layoutInflater.inflate(R.layout.quiz_definition_view, quiz_question_container_view, false)
        quiz_question_container_view.addView(definitionView)
        (definitionView.quiz_question_text_view as TextView).text = question.questionText
    }

    private fun showGlyphQuestion(question: GlyphQuestion) {
        val glyphView = layoutInflater.inflate(R.layout.quiz_glyph_view, quiz_question_container_view, false)
        quiz_question_container_view.addView(glyphView)
        Glide.with(this)
                .load(question.questionGlyph)
                .into(glyphView.quiz_question_glyph_view)
    }

    private fun showAnswers(answers: List<Answer>) {
        val model = mQuizViewModel ?: return
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
        val model = mQuizViewModel ?: return
        if (mOnTickDisposable?.isDisposed == false) {
            mOnTickDisposable?.dispose()
        }
        mOnTickDisposable = model.onTick()
                .subscribeWith(object : ResourceSubscriber<Long>() {
                    override fun onStart() {
                        super.onStart()
                        quiz_countdown_timer.max = QuizViewModel.MAX_TIMER_VALUE.toInt()
                        quiz_countdown_timer.progress = quiz_countdown_timer.max
                    }

                    override fun onComplete() {
                        handleResult(model.answerQuestion())
                    }

                    override fun onError(t: Throwable?) {
                        Log.e(TAG, "Something terrible happened?", t)
                    }

                    override fun onNext(t: Long) {
                        quiz_countdown_timer.progress = t.toInt()
                        //No one man should have all these primitive casts.
                        setTimerColor((t.toInt() / quiz_countdown_timer.max.toFloat()))
                    }
                })
    }

    private fun setTimerColor(ratio: Float) {
        val red = Math.abs(ratio * Color.red(mCorrectColor) + (1 - ratio) * Color.red(mIncorrectColor)).toInt()
        val green = Math.abs(ratio * Color.green(mCorrectColor) + (1 - ratio) * Color.green(mIncorrectColor)).toInt()
        val blue = Math.abs(ratio * Color.blue(mCorrectColor) + (1 - ratio) * Color.blue(mIncorrectColor)).toInt()
        quiz_countdown_timer.progressDrawable.setColorFilter(Color.rgb(red, green, blue), PorterDuff.Mode.MULTIPLY)
    }

    @Suppress("ConvertLambdaToReference") //It can't be done. quiz_answer_view's type is too confusing.
    private fun handleResult(correct: Boolean, selected: View? = null) {
        if (correct) {
            selected?.setBackgroundColor(mCorrectColor)
        } else {
            selected?.setBackgroundColor(mIncorrectColor)
            val model = mQuizViewModel ?: return
            val answer = model.getCorrectAnswer()
            (0 until quiz_answer_view.childCount)
                    .map { quiz_answer_view.getChildAt(it) }
                    .first { it.tag == answer }
                    .setBackgroundColor(mCorrectColor)

        }
        mHandler.postDelayed(this::instantiateFromModel, RESULT_DELAY)
    }
}