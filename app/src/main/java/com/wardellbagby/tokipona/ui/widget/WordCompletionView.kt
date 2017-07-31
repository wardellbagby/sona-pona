package com.wardellbagby.tokipona.ui.widget

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tokenautocomplete.TokenCompleteTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.model.Word

/**
 * @author Wardell Bagby
 */
class WordCompletionView : TokenCompleteTextView<Word> {

    interface OnWordClickedListener {
        fun onWordClicked(word: Word)
    }

    private var listener: OnWordClickedListener? = null


    var isGlossed = false
        set(value) {
            field = value
            val objects = objects
            clear()
            if (objects.isNotEmpty()) {
                objects.forEach {
                    addObject(it)
                }
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        /* We set the inputType to "textVisiblePassword" in xml, but that makes the font monospaced.
           this reverts it back to the default font. Setting the fontFamily in xml didn't work for
           some reason.
         */
        typeface = Typeface.DEFAULT
    }

    override fun getViewForObject(word: Word?): View {
        val view: TextView = LayoutInflater.from(context).inflate(R.layout.word_token, parent as ViewGroup, false) as TextView
        view.text = if (isGlossed) word?.gloss else word?.name
        val background = view.background
        if (word?.definitions?.isEmpty() ?: false) {
            background.state = intArrayOf(R.attr.state_error)
        } else {
            background.state = intArrayOf(-R.attr.state_error)
        }

        if (word != null)
            view.setOnClickListener { listener?.onWordClicked(word) }
        return view
    }

    override fun defaultObject(completionText: String?): Word {
        return Word(completionText ?: "")
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = Bundle(2) //Never seen anybody do this before but it works!
        state.putParcelable("super", superState)
        state.putBoolean("isGlossed", isGlossed)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            isGlossed = state.getBoolean("isGlossed")
            super.onRestoreInstanceState(state.getParcelable("super"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun getText(): Editable {
        return super.getText()
    }
}

