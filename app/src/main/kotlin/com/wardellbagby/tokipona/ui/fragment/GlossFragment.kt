package com.wardellbagby.tokipona.ui.fragment

import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.util.Words
import com.wardellbagby.tokipona.util.emptyString
import com.wardellbagby.tokipona.util.subscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gloss.*

/**
 * A fragment for glossing [Word]s into English.
 */
class GlossFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_gloss, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        if (rootView == null) return
        glossed_display_view.setTextAnimations(context, R.anim.fade_in_quick, R.anim.fade_out_quick)
    }

    override fun onResume() {
        super.onResume()
        context.subscribe(this::onGlossEvent)
        subscribeToTextChanges()
    }

    override fun getSupportedTransitionNames(): List<String> =
            listOf(R.string.transition_name_main_content, R.string.transition_name_extra_content).map(this::getString)

    private fun subscribeToTextChanges() {
        RxTextView.afterTextChangeEvents(inputted_text)
                .filter { it.editable().isNullOrEmpty() }
                .subscribe { glossed_display_view.setGlossedText(emptyString()) }
                .attach()

        RxTextView.afterTextChangeEvents(inputted_text)
                .map { it.editable().isNullOrEmpty() }
                .map { if (it) View.INVISIBLE else View.VISIBLE }
                .subscribe(this::setVisibilityForShareButtons)
                .attach()

        RxTextView.afterTextChangeEvents(inputted_text)
                .filter { !it.editable().isNullOrEmpty() }
                .flatMapSingle { Single.just(it.view().text.toString()).zipWith(Words.getWords(context)) }
                .flatMapSingle { Words.glossToText(it.first, it.second) }
                .map(String::trim)
                .filter { glossed_display_view.getGlossedText() != it }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(glossed_display_view::setGlossedText).attach()
    }

    private fun setVisibilityForShareButtons(visibility: Int) {
        val transition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) Explode() else Fade()
        TransitionManager.beginDelayedTransition(glossed_display_view, transition)
        glossed_display_view.setSharePaneVisibility(visibility)
    }

    private fun onGlossEvent(event: MainActivity.GlossEvent) {
        inputted_text.setText(event.glossableText)
    }
}
