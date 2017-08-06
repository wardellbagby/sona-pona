package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.tokenautocomplete.TokenCompleteTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.ui.widget.WordCompletionView
import com.wardellbagby.tokipona.util.Words
import com.wardellbagby.tokipona.util.subscribe

/**
 * A fragment for glossing [Word]s into English and back again.
 */
class GlossFragment : BaseFragment() {

    private var mWordCompletionView: WordCompletionView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_gloss, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        //todo For some reason, clicking on a span causes it to be de-highlighted?
        mWordCompletionView = rootView?.findViewById<WordCompletionView>(R.id.entered_text)
        Words.getWords(context, {
            val adapter = ArrayAdapter<Word>(context, android.R.layout.simple_list_item_1, it)
            mWordCompletionView?.apply {
                setAdapter(adapter)
                setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.SelectDeselect)
                allowCollapse(true)
                setSplitChar(Words.DELIMITERS.toCharArray())
                allowDuplicates(true)
                threshold = 1
                isLongClickable = true
            }
            rootView?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
                mWordCompletionView?.apply { isGlossed = !isGlossed }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        context.subscribe(this::setText)
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_fab).map(this::getString)
    }

    fun setText(event: MainActivity.GlossEvent) {
        mWordCompletionView?.clear()
        Words.getWords(context) {
            Words.convertToWords(event.glossableText, it, {
                it.forEach {
                    mWordCompletionView?.addObject(it, it.name)
                }
            })
        }

    }
}
