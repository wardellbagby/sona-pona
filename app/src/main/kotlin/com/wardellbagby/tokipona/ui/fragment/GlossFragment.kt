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
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.util.Words
import com.wardellbagby.tokipona.util.emptyString
import com.wardellbagby.tokipona.util.subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gloss.*

/**
 * A fragment for glossing [Word]s into English and back again.
 */
class GlossFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_gloss, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        if (rootView == null) {
            return
        }
        glossed_display_view.setTextAnimations(context, R.anim.fade_in_quick, R.anim.fade_out_quick)

        inputted_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    setVisibilityForShareButtons(View.INVISIBLE)
                    glossed_display_view.setGlossedText(emptyString())
                    return
                } else {
                    setVisibilityForShareButtons(View.VISIBLE)
                }
                Words.getWords(context)
                        .flatMap {
                            Words.glossToText(editable.toString(), it)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { it ->
                            if (glossed_display_view.getGlossedText() != it.trim()) {
                                glossed_display_view.setGlossedText(it)
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
        return listOf(R.string.transition_name_main_content, R.string.transition_name_extra_content).map(this::getString)
    }

    private fun setVisibilityForShareButtons(visibility: Int) {
        val transition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) Explode() else Fade()
        TransitionManager.beginDelayedTransition(glossed_display_view, transition)
        glossed_display_view.setSharePaneVisibility(visibility)
    }

    @Suppress("ConvertLambdaToReference") //It can't be done. inputted_text's type is too confusing.
    private fun onGlossEvent(event: MainActivity.GlossEvent) {
        inputted_text.setText(event.glossableText)
    }
}
