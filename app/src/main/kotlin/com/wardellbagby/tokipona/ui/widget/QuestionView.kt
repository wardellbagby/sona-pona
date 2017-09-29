package com.wardellbagby.tokipona.ui.widget

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.DefinitionQuestion
import com.wardellbagby.tokipona.data.GlyphQuestion
import com.wardellbagby.tokipona.data.Question

/**
 * A View representing that can handle displaying a [Question]
 * @author Wardell
 */
class QuestionView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        CardView(context, attrs, defStyleAttr) {

    /**
     * Sets the [Question] to be displayed by this view.
     */
    fun setQuestion(question: Question?) {
        visibility = View.VISIBLE
        removeAllViews()
        when (question) {
            is DefinitionQuestion -> showDefinitionQuestion(question)
            is GlyphQuestion -> showGlyphQuestion(question)
            null -> visibility = View.INVISIBLE
        }
    }

    private fun showDefinitionQuestion(question: DefinitionQuestion) {
        val definitionView = LayoutInflater.from(context).inflate(R.layout.default_autosize_text_view, this, false) as TextView
        addView(definitionView)
        definitionView.text = question.questionText
    }

    private fun showGlyphQuestion(question: GlyphQuestion) {
        val glyphView = LayoutInflater.from(context).inflate(R.layout.quiz_glyph_view, this, false) as ImageView
        addView(glyphView)
        Glide.with(this)
                .load(question.questionGlyph)
                .into(glyphView)
    }
}