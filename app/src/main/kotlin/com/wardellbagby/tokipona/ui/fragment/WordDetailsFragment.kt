package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.adapter.WordDetailsAdapter

/**
 * Show the details (currently, just the definitions) of a [Word]
 *
 * @author Wardell Bagby
 */
class WordDetailsFragment : BaseFragment() {
    companion object {
        const val WORD = "WORD"
    }

    private val displayedWord: Word by lazy {
        val word = arguments.getParcelable<Word>(WORD)
        state(word)
        word
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_word_details, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.definition_list)
        recyclerView.adapter = WordDetailsAdapter(displayedWord)
        return rootView
    }

    override fun getTitle(): CharSequence? {
        return displayedWord.name
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_main_content).map(this::getString)
    }
}
