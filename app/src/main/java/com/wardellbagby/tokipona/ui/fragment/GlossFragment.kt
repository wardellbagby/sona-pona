package com.wardellbagby.tokipona.ui.fragment


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.tokenautocomplete.TokenCompleteTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.model.Word
import com.wardellbagby.tokipona.ui.widget.WordCompletionView
import com.wardellbagby.tokipona.util.Words


/**
 * A fragment for glossing [Word]s into English and back again.
 */
class GlossFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_gloss, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        //todo For some reason, clicking on a span causes it to be de-highlighted?
        val inputtedText = rootView?.findViewById<WordCompletionView>(R.id.entered_text)
        Words.getWords(context, {
            val adapter = ArrayAdapter<Word>(context, android.R.layout.simple_list_item_1, it)
            inputtedText?.apply {
                setAdapter(adapter)
                setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.SelectDeselect)
                allowCollapse(true)
                setSplitChar(Words.DELIMITERS.toCharArray())
                allowDuplicates(true)
                threshold = 1
                isLongClickable = true
            }
            rootView?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
                inputtedText?.apply { isGlossed = !isGlossed }
            }
        })
    }
}
