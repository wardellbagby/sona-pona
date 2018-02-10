package com.wardellbagby.tokipona.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.wardellbagby.tokipona.R

class WordDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val partOfSpeechView = view.findViewById<View>(R.id.part_of_speech) as TextView
    val definitionView = view.findViewById<View>(R.id.word_definition) as TextView
}