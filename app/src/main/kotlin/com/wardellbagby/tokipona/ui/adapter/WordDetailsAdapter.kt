package com.wardellbagby.tokipona.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.viewholder.WordDetailsViewHolder

/**
 * An adapter for displaying the definition of a [Word] in a RecyclerView.
 *
 * @param word: The [Word] that contains the information to display.
 */
class WordDetailsAdapter(private val word: Word) : RecyclerView.Adapter<WordDetailsViewHolder>() {
    override fun getItemCount(): Int {
        return word.definitions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.word_details_content, parent, false)
        return WordDetailsViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: WordDetailsViewHolder, position: Int) {
        val definition = word.definitions[position]
        viewHolder.partOfSpeechView.text = definition.partOfSpeech.toString()
        viewHolder.definitionView.text = definition.definitionText
    }
}