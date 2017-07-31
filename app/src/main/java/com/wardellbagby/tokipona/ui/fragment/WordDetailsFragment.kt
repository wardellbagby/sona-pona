package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wardellbagby.tokipona.R

import com.wardellbagby.tokipona.model.Word

/**
 * Show the details (currently, just the definitions) of a [Word]
 *
 * @author Wardell Bagby
 */
class WordDetailsFragment : BaseFragment() {
    companion object {
        val WORD = "word"
    }

    private var mWord: Word? by state<Word?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(WORD)) {
            mWord = arguments.getParcelable<Word>(WORD)
            val activity = this.activity
            val toolbar = activity.findViewById<View>(R.id.toolbar) as Toolbar?
            toolbar?.title = mWord?.name
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.word_detail, container, false)

        val recyclerView = rootView?.findViewById<View>(R.id.definition_list) as RecyclerView
        recyclerView.adapter = WordDefinitionsAdapter()

        return rootView
    }

    override fun getTitle(): CharSequence? {
        return mWord?.name
    }

    class DefinitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partOfSpeechView = view.findViewById<View>(R.id.part_of_speech) as TextView
        val definitionView = view.findViewById<View>(R.id.word_definition) as TextView
    }

    inner class WordDefinitionsAdapter : RecyclerView.Adapter<DefinitionViewHolder>() {
        override fun getItemCount(): Int {
            return mWord?.definitions?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.word_detail_content, parent, false)
            return DefinitionViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: DefinitionViewHolder, position: Int) {
            val definition = mWord?.definitions?.get(position)
            viewHolder.partOfSpeechView.text = definition?.partOfSpeech.toString()
            viewHolder.definitionView.text = definition?.definitionText

        }

    }
}
