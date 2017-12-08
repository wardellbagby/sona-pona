package com.wardellbagby.tokipona.ui.adapter

import android.content.Context
import android.os.Build
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.provider.GlyphContentProvider
import com.wardellbagby.tokipona.ui.SortedWordListCallback
import com.wardellbagby.tokipona.ui.viewholder.WordViewHolder
import com.wardellbagby.tokipona.util.emptyString
import com.wardellbagby.tokipona.util.plusAssign

/**
 * An adapter for displaying [Word]s in a RecyclerView.
 *
 * @param context: A valid Context
 * @param providedWords: A Collection of [Word]s that will be shown by this adapter.
 * @param onWordClicked: A listener that will be invoked when a word is clicked. Returns whether or not the clicked word should be set as selected.
 */
class WordListAdapter(private val context: Context, private val providedWords: Collection<Word>, private var onWordClicked: ((Word) -> Boolean) = { _ -> false }) : RecyclerView.Adapter<WordViewHolder>() {
    private val sortedListCallback = SortedWordListCallback(this)

    private val currentValues: SortedList<Word> = SortedList<Word>(Word::class.java, sortedListCallback)
    private var currentFilterText: String = emptyString()
        set(value) {
            sortedListCallback.filterText = value
            field = value
        }

    private var selectedWord: Word? = null

    init {
        currentValues.beginBatchedUpdates()
        currentValues += providedWords
        currentValues.endBatchedUpdates()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_content, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.word = currentValues.get(position) ?: return
        holder.name.text = createHighlightedFilteredText(holder.word.name)
        holder.definition.text = createHighlightedFilteredText(holder.word.definitions.first().definitionText)
        Glide.with(context)
                .asBitmap()
                .load(GlyphContentProvider.getUriForWord(holder.word))
                .into(holder.icon)

        holder.icon.contentDescription = holder.word.name

        //todo Almost certain I can just use a ColorStateList for this...
        if (selectedWord == holder.word) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.contentView.setBackgroundColor(context.resources.getColor(R.color.colorAccent, context.theme))
            } else {
                @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
                holder.contentView.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
            }
        } else {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val ta = context.obtainStyledAttributes(attrs)
            holder.contentView.background = ta.getDrawable(0)
            ta.recycle()
        }

        holder.view.setOnClickListener { _ ->
            if (onWordClicked(holder.word)) {
                selectedWord = holder.word
                notifyDataSetChanged()
            }
        }

    }

    private fun createHighlightedFilteredText(text: String): CharSequence {
        if (currentFilterText.isBlank()) {
            return text
        }
        val filterText = currentFilterText
        val highlightedText = SpannableStringBuilder(text)
        val start = text.indexOf(filterText, ignoreCase = true)
        val end = start + filterText.length
        if (start < 0 || end > text.length) {
            return text
        }
        @Suppress("DEPRECATION") // Necessary evil until minSdk is Marshmallow.
        highlightedText.setSpan(BackgroundColorSpan(context.resources.getColor(R.color.colorAccent)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return highlightedText
    }

    override fun getItemCount(): Int {
        return currentValues.size()
    }

    /**
     * Filters the word list using the provided [text]
     */
    fun filter(text: String) {
        if (currentFilterText == text) return
        currentFilterText = text
        currentValues.beginBatchedUpdates()
        currentValues.clear()
        currentValues += providedWords.filter { containsText(it, text) }.map { createFilteredWord(it, text) }
        currentValues.endBatchedUpdates()
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
}