package com.wardellbagby.tokipona.ui.fragment

import android.os.Build
import android.os.Bundle
import android.support.v7.util.SortedList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.widget.RxTextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.provider.GlyphContentProvider
import com.wardellbagby.tokipona.util.Words
import com.wardellbagby.tokipona.util.emptyString
import com.wardellbagby.tokipona.util.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_word_list.*
import java.util.concurrent.TimeUnit

/**
 * todo Use ViewModel for this.
 * @author Wardell Bagby
 */
class WordListFragment : BaseFragment() {

    private lateinit var mAdapter: SimpleItemRecyclerViewAdapter
    private var mListener: ((Word) -> Boolean)? = null
    private var mScrollPosition by state(0)
    private var mSelectedWord: Word? by state(null)

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
        mScrollPosition = (word_list.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0
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
    fun setOnWordSelectedCallback(listener: ((Word) -> Boolean)?) {
        mListener = listener
    }

    private fun setupRecyclerView() {
        //todo This should maybe show a loading bar?
        Words.getWords(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWordListReady)
                .also(this::disposeOnPause)
    }

    private fun onWordListReady(words: List<Word>) {
        RxTextView.afterTextChangeEvents(search_edit_text)
                .debounce(400L, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val text = it.editable()?.toString() ?: return@subscribe
                    mAdapter.filter(text)
                }
        TransitionManager.beginDelayedTransition(word_list)
        mAdapter = SimpleItemRecyclerViewAdapter(words)
        word_list.adapter = mAdapter
        (word_list.layoutManager as LinearLayoutManager).scrollToPosition(mScrollPosition)
        word_list.addOnScrollListener(mOnScrollListener)
    }

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            mScrollPosition = (word_list.layoutManager as? LinearLayoutManager)
                    ?.findFirstCompletelyVisibleItemPosition() ?: 0
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }
    }

    inner class SimpleItemRecyclerViewAdapter(private val mValues: Collection<Word>) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private val mWords: SortedList<Word>
        private var mFilterText: String = emptyString()

        init {
            mWords = SortedList<Word>(Word::class.java, object : SortedList.Callback<Word>() {
                override fun onChanged(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeChanged(p0, p1)
                }

                override fun onInserted(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeInserted(p0, p1)
                }

                override fun compare(left: Word, right: Word): Int {
                    if (mFilterText.isBlank()) return left.name.compareTo(right.name)
                    val filter = mFilterText
                    val filterInLeftName = filter in left.name
                    val filterInRightName = filter in right.name
                    if (filterInLeftName && !filterInRightName) {
                        return -1
                    } else if (!filterInLeftName && filterInRightName) {
                        return 1
                    } else if (filterInLeftName && filterInRightName) {
                        return left.name.compareTo(right.name)
                    }
                    val leftDefinition = left.definitions.first().definitionText
                    val rightDefinition = right.definitions.first().definitionText
                    val filterInLeftDef = filter in leftDefinition
                    val filterInRightDef = filter in rightDefinition
                    return if (filterInLeftDef && !filterInRightDef) {
                        -1
                    } else if (!filterInLeftDef && filterInRightDef) {
                        1
                    } else {
                        leftDefinition.compareTo(rightDefinition)
                    }
                }

                override fun areItemsTheSame(p0: Word?, p1: Word?): Boolean {
                    return p0 == p1
                }

                override fun onRemoved(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeRemoved(p0, p1)
                }

                override fun areContentsTheSame(p0: Word?, p1: Word?): Boolean {
                    return areItemsTheSame(p0, p1)
                }

                override fun onMoved(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemMoved(p0, p1)
                }
            })
            mWords.beginBatchedUpdates()
            mWords += mValues
            mWords.endBatchedUpdates()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemRecyclerViewAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SimpleItemRecyclerViewAdapter.ViewHolder, position: Int) {
            holder.word = mWords.get(position) ?: return
            holder.name.text = createHighlightedFilteredText(holder.word.name)
            holder.definition.text = createHighlightedFilteredText(holder.word.definitions.first().definitionText)
            Glide.with(this@WordListFragment)
                    .asBitmap()
                    .load(GlyphContentProvider.getUriForWord(holder.word))
                    .into(holder.icon)

            holder.icon.contentDescription = holder.word.name

            //todo Almost certain I can just use a ColorStateList for this...
            if (mSelectedWord == holder.word) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.contentView.setBackgroundColor(resources.getColor(R.color.colorAccent, context.theme))
                } else {
                    @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
                    holder.contentView.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            } else {
                val attrs = intArrayOf(android.R.attr.selectableItemBackground)
                val ta = context.obtainStyledAttributes(attrs)
                holder.contentView.background = ta.getDrawable(0)
                ta.recycle()
            }

            holder.view.setOnClickListener { _ ->
                if (mListener?.invoke(holder.word) != false) {
                    mSelectedWord = holder.word
                    notifyDataSetChanged()
                }
            }

        }

        private fun createHighlightedFilteredText(text: String): CharSequence {
            if (mFilterText.isBlank()) {
                return text
            }
            val filterText = mFilterText
            val highlightedText = SpannableStringBuilder(text)
            val start = text.indexOf(filterText, ignoreCase = true)
            val end = start + filterText.length
            if (start < 0 || end > text.length) {
                return text
            }
            @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
            highlightedText.setSpan(BackgroundColorSpan(resources.getColor(R.color.colorAccent)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return highlightedText
        }

        override fun getItemCount(): Int {
            return mWords.size()
        }

        /**
         * Filters the word list using the provided [text]
         */
        fun filter(text: String) {
            if (mFilterText == text) return
            mFilterText = text
            TransitionManager.beginDelayedTransition(word_list)
            mWords.beginBatchedUpdates()
            mWords.clear()
            mWords += mValues.filter { containsText(it, text) }.map { createFilteredWord(it, text) }
            mWords.endBatchedUpdates()
        }

        private fun createFilteredWord(word: Word, text: String): Word {
            if (text.isBlank()) return word
            return word.definitions.firstOrNull {
                text.toLowerCase() in it.definitionText.toLowerCase()
            }.let {
                Word(word.name, listOf(it ?: return word))
            }
        }

        private fun containsText(item: Word, text: String): Boolean {
            return when {
                text.isEmpty() -> true
                text.toLowerCase() in item.name -> true
                else -> item.definitions.any { text.toLowerCase() in it.definitionText.toLowerCase() }
            }
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.icon)
            val name: TextView = view.findViewById(R.id.name)
            val definition: TextView = view.findViewById(R.id.definition)
            val contentView: ViewGroup = view.findViewById(R.id.content)
            lateinit var word: Word
        }
    }
}
