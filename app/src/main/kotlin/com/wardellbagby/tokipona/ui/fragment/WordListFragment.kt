package com.wardellbagby.tokipona.ui.fragment

import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.util.SortedList
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bowyer.app.fabtoolbar.FabToolbar
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.util.Words
import org.droidparts.widget.ClearableEditText

/**
 * @author Wardell Bagby
 */
class WordListFragment : BaseFragment() {

    private var mSearchEditText: ClearableEditText? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SimpleItemRecyclerViewAdapter? = null
    private var mFabToolbar: FabToolbar? = null
    private var mListener: ((Word) -> Boolean)? = null
    private var mScrollPosition by state(0)
    private var mSelectedWord: Word? by state(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_word_list, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        val fab = rootView?.findViewById<FloatingActionButton>(R.id.fab)
        mFabToolbar = rootView?.findViewById<FabToolbar>(R.id.fab_toolbar)
        mFabToolbar?.setFab(fab)
        fab?.setOnClickListener { mFabToolbar?.expandFab() }

        mSearchEditText = mFabToolbar?.findViewById<ClearableEditText>(R.id.search_edit_text)
        mSearchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().toLowerCase()
                mAdapter?.filter(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        mSearchEditText?.setListener {
            mSearchEditText?.text?.clear()
            mFabToolbar?.slideInFab()
        }

        mRecyclerView = rootView?.findViewById<RecyclerView>(R.id.word_list)
        setupRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        mScrollPosition = (mRecyclerView?.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0
    }

    override fun onBackPressed(): Boolean {
        if (mFabToolbar?.isFabExpanded ?: false) {
            mSearchEditText?.text?.clear()
            mFabToolbar?.slideInFab()
            return true
        }
        return false
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_fab, R.string.transition_name_list)
                .map(this::getString)
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
        Words.getWords(context, {
            mAdapter = SimpleItemRecyclerViewAdapter(it)
            mRecyclerView?.adapter = mAdapter
            (mRecyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(mScrollPosition)
            mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    mScrollPosition = (mRecyclerView?.layoutManager as? LinearLayoutManager)
                            ?.findFirstCompletelyVisibleItemPosition() ?: 0
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        })
    }

    inner class SimpleItemRecyclerViewAdapter(private val mValues: Collection<Word>) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private var mWords: SortedList<Word>? = null

        init {
            mWords = SortedList<Word>(Word::class.java, object : SortedList.Callback<Word>() {
                override fun onChanged(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeChanged(p0, p1)
                }

                override fun onInserted(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeInserted(p0, p1)
                }

                override fun compare(p0: Word?, p1: Word?): Int {
                    return p0?.name?.compareTo(p1?.name ?: "") ?: 0
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
            mWords?.beginBatchedUpdates()
            for (word in mValues) {
                mWords?.add(word)
            }
            mWords?.endBatchedUpdates()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemRecyclerViewAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SimpleItemRecyclerViewAdapter.ViewHolder, position: Int) {
            holder.word = mWords?.get(position)
            holder.name.text = holder.word?.name
            holder.definition.text = holder.word?.definitions?.get(0)?.definitionText

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
                if (mListener?.invoke(holder.word ?: Word()) ?: true) {
                    mSelectedWord = holder.word
                    notifyDataSetChanged()
                }
            }

        }

        override fun getItemCount(): Int {
            return mWords?.size() ?: 0
        }

        fun filter(text: String) {
            mWords?.beginBatchedUpdates()
            mWords?.clear()
            mWords?.addAll(mValues.filter { containsText(it, text) })
            mWords?.endBatchedUpdates()

        }

        private fun containsText(item: Word, text: String): Boolean {
            when {
                text.toLowerCase() in item.name -> return true
                else                            -> return item.definitions.any { text.toLowerCase() in it.definitionText.toLowerCase() }
            }
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById<View>(R.id.name) as TextView
            val definition: TextView = view.findViewById<View>(R.id.definition) as TextView
            val contentView: ViewGroup = view.findViewById(R.id.content)
            var word: Word? = null
        }
    }
}
