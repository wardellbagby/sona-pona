package com.wardellbagby.tokipona.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.data.Word

class WordViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val icon: ImageView = view.findViewById(R.id.icon)
    val name: TextView = view.findViewById(R.id.name)
    val definition: TextView = view.findViewById(R.id.definition)
    val contentView: ViewGroup = view.findViewById(R.id.content)
    lateinit var word: Word
}