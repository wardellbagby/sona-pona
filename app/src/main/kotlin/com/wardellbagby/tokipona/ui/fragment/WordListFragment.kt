package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.jakewharton.rxbinding2.widget.RxTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.adapter.WordListAdapter
import com.wardellbagby.tokipona.util.Words
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_word_list.*
import java.util.concurrent.TimeUnit

/**
 * todo Use ViewModel for this?
 * @author Wardell Bagby
 */
class WordListFragment : BaseFragment() {

    companion object {
        const val FILTER_TEXT_DEBOUNCE_MILLIS = 400L
    }

    private lateinit var wordListAdapter: WordListAdapter
    private var currentScrollPosition by state(0)
    private var onWordSelected: ((Word) -> Boolean) = { _ -> false }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_word_list, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        fab_toolbar.setFab(fab)
        /*Fixes an issue where the word list wouldn't properly expand to fill the space of the toolbar
          and where the fab would animate to 0,0 when first clicked.
         */
        fab_toolbar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fab_toolbar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                fab_toolbar.visibility = View.GONE
            }
        })
        fab.setOnClickListener {
            TransitionManager.beginDelayedTransition(word_list, AutoTransition())
            fab_toolbar.visibility = View.VISIBLE
            fab_toolbar.expandFab()
        }
        search_edit_text.setListener {
            TransitionManager.beginDelayedTransition(word_list, AutoTransition())
            search_edit_text.text?.clear()
            fab_toolbar.contractFab()
            fab_toolbar.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        currentScrollPosition = (word_list.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    override fun onBackPressed(): Boolean {
        if (fab_toolbar.isFabExpanded) {
            TransitionManager.beginDelayedTransition(word_list, AutoTransition())
            search_edit_text.text.clear()
            fab_toolbar.slideInFab()
            fab_toolbar.visibility = View.GONE
            return true
        }
        return false
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_main_content).map(this::getString)
    }

    override fun getTargetsToExcludeFromTransitions(): List<View> {
        return listOf(fab_toolbar)
    }

    /**
     * Sets a callback that will be invoked when a [Word] is selected by the user.
     * This callback will receive the [Word] that was selected, and should return a boolean
     * stating whether or not the word should show as selected in the [WordListFragment]
     */
    fun setOnWordSelectedCallback(onWordSelected: ((Word) -> Boolean)) {
        this.onWordSelected = onWordSelected
    }

    private fun setupRecyclerView() {
        //todo This should maybe show a loading bar?
        Words.getWords(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWordListReady)
                .attach()
    }

    private fun onWordListReady(words: List<Word>) {
        RxTextView.afterTextChangeEvents(search_edit_text)
                .debounce(FILTER_TEXT_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val text = it.editable()?.toString() ?: return@subscribe
                    TransitionManager.beginDelayedTransition(word_list)
                    wordListAdapter.filter(text)
                }.attach()

        wordListAdapter = WordListAdapter(activity, words) {
            onWordSelected(it)
        }
        word_list.adapter = wordListAdapter
        (word_list.layoutManager as LinearLayoutManager).scrollToPosition(currentScrollPosition)
        word_list.addOnScrollListener(onScrollListener)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            currentScrollPosition = (word_list.layoutManager as? LinearLayoutManager)
                    ?.findFirstCompletelyVisibleItemPosition() ?: 0
        }
    }
}
