package com.wardellbagby.tokipona.ui.fragment


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.util.SortedList
import android.support.v7.util.SortedList.Callback
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bowyer.app.fabtoolbar.FabToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.model.Word
import org.droidparts.widget.ClearableEditText
import java.io.InputStreamReader

/**
 * @author Wardell Bagby
 */
class WordListFragment : BaseFragment() {

    private var mTwoPane: Boolean = false
    private var mAdapter: SimpleItemRecyclerViewAdapter? = null
    private var mFabToolbar: FabToolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_word_list, container, false)
        return rootView
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        val fab = rootView?.findViewById<View>(R.id.fab) as FloatingActionButton
        mFabToolbar = rootView.findViewById<View>(R.id.fab_toolbar) as FabToolbar
        mFabToolbar?.setFab(fab)
        fab.setOnClickListener { mFabToolbar?.expandFab() }
        val searchEditText = mFabToolbar?.findViewById<View>(R.id.search_edit_text) as ClearableEditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = searchEditText.text.toString().toLowerCase()
                mAdapter?.filter(text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        searchEditText.setListener {
            mFabToolbar?.slideInFab()
        }

        val recyclerView = rootView.findViewById<View?>(R.id.word_list)
        setupRecyclerView(recyclerView as RecyclerView)

        mTwoPane = rootView.findViewById<View>(R.id.word_detail_container) != null
    }

    override fun onBackPressed(): Boolean {
        if (mFabToolbar?.isFabExpanded ?: false) {
            mFabToolbar?.slideInFab()
            return true
        }
        return false
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val gson = Gson()
        val reader = InputStreamReader(resources.assets.open("word_list.json"))
        val words: List<Word> = gson.fromJson(reader, object : TypeToken<List<Word>>() {}.type)
        mAdapter = SimpleItemRecyclerViewAdapter(words)
        recyclerView.adapter = mAdapter
    }

    inner class SimpleItemRecyclerViewAdapter(private val mValues: Collection<Word>) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
        private var mWords: SortedList<Word>? = null

        init {
            mWords = SortedList<Word>(Word::class.java, object : Callback<Word>() {
                override fun onChanged(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeChanged(p0, p1)
                }

                override fun onInserted(p0: Int, p1: Int) {
                    this@SimpleItemRecyclerViewAdapter.notifyItemRangeInserted(p0, p1)
                }

                override fun compare(p0: Word?, p1: Word?): Int {
                    return p0!!.name.compareTo(p1!!.name)
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
            holder.mItem = mWords!![position]
            holder.mIdView.text = mWords!![position].name
            holder.mContentView.text = mWords!![position].definitions[0].definitionText

            holder.mView.setOnClickListener { _ ->
                val manager = if (mTwoPane) childFragmentManager else fragmentManager
                val layoutId = if (mTwoPane) R.id.word_detail_container else R.id.frameLayout

                var fragment: Fragment? = manager.findFragmentByTag(holder.mItem?.name)
                if (fragment == null) {
                    fragment = WordDetailFragment()
                    fragment.arguments = Bundle().apply { putParcelable(WordDetailFragment.Companion.WORD, holder.mItem) }
                }

                manager.beginTransaction()
                        .replace(layoutId, fragment)
                        .addToBackStack(R.id.navigation_dictionary.toString())
                        .commit()
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
                else -> return item.definitions.any { text.toLowerCase() in it.definitionText.toLowerCase() }
            }
        }

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.findViewById<View>(R.id.id) as TextView
            val mContentView: TextView = mView.findViewById<View>(R.id.content) as TextView
            var mItem: Word? = null

            override fun toString(): String {
                return super.toString() + " '" + mContentView.text + "'"
            }
        }
    }
}
