package com.wardellbagby.tokipona.ui

import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import com.wardellbagby.tokipona.data.Word
import com.wardellbagby.tokipona.ui.viewholder.WordViewHolder
import com.wardellbagby.tokipona.util.emptyString

class SortedWordListCallback(private val adapter: RecyclerView.Adapter<WordViewHolder>) : SortedList.Callback<Word>() {
    var filterText: String = emptyString()

    override fun onChanged(p0: Int, p1: Int) {
        adapter.notifyItemRangeChanged(p0, p1)
    }

    override fun onInserted(p0: Int, p1: Int) {
        adapter.notifyItemRangeInserted(p0, p1)
    }

    override fun compare(left: Word, right: Word): Int {
        if (filterText.isBlank()) return left.name.compareTo(right.name)
        val filter = filterText
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
        adapter.notifyItemRangeRemoved(p0, p1)
    }

    override fun areContentsTheSame(p0: Word?, p1: Word?): Boolean {
        return areItemsTheSame(p0, p1)
    }

    override fun onMoved(p0: Int, p1: Int) {
        adapter.notifyItemMoved(p0, p1)
    }
}