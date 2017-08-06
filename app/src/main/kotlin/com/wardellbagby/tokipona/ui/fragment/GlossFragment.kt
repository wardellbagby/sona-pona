package com.wardellbagby.tokipona.ui.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.overlay.widget.GlossedDisplayView
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.util.Words
import com.wardellbagby.tokipona.util.emptyString
import com.wardellbagby.tokipona.util.subscribe

/**
 * A fragment for glossing [Word]s into English and back again.
 */
class GlossFragment : BaseFragment() {

    private lateinit var mGlossedDisplayView: GlossedDisplayView
    private lateinit var mInputtedText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_gloss, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        if (rootView == null) {
            return
        }

        mGlossedDisplayView = rootView.findViewById<GlossedDisplayView>(R.id.transition_root)

        mInputtedText = rootView.findViewById<EditText>(R.id.inputted_text)
        mGlossedDisplayView.setTextAnimations(context, R.anim.fade_in_quick, R.anim.fade_out_quick)

        mInputtedText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    setVisibilityForShareButtons(View.INVISIBLE)
                    mGlossedDisplayView.setGlossedText(emptyString())
                    return
                } else {
                    setVisibilityForShareButtons(View.VISIBLE)
                }
                Words.getWords(context) {
                    Words.glossToString(editable.toString(), it, true) {
                        if (mGlossedDisplayView.getGlossedText() != it.trim()) {
                            mGlossedDisplayView.setGlossedText(it)
                        }
                    }
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        context.subscribe(this::onGlossEvent)
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_list).map(this::getString)
    }

    private fun setVisibilityForShareButtons(visibility: Int) {
        val transition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) Explode() else Fade()
        TransitionManager.beginDelayedTransition(mGlossedDisplayView, transition)
        mGlossedDisplayView.setSharePaneVisibility(visibility)
    }

    private fun onGlossEvent(event: MainActivity.GlossEvent) {
        mGlossedDisplayView.setGlossedText(emptyString())
        Words.getWords(context) {
            //Set it on the input so it can be cleared and the animations will start.
            Words.glossToString(event.glossableText, it, true, mInputtedText::setText)
        }

    }
}
