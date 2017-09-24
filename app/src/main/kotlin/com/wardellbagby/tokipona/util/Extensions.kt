package com.wardellbagby.tokipona.util

import android.content.Context
import android.support.v4.app.FragmentManager
import com.wardellbagby.tokipona.ui.activity.BaseActivity
import com.wardellbagby.tokipona.ui.fragment.BaseFragment
import java.util.Random

/**
 * @author Wardell Bagby
 */

private const val EMPTY_STRING = ""

fun FragmentManager.getLastBackStackEntry(): FragmentManager.BackStackEntry? {
    return if (backStackEntryCount == 0) null else getBackStackEntryAt(backStackEntryCount - 1)
}

fun FragmentManager.sendOnBackPressed(): Boolean {
    return fragments
            .filter { it.isAdded && it.isVisible && !it.isRemoving && it.isResumed }
            .any { it is BaseFragment && it.onBackPressed() }
}

fun <EventType : BaseActivity.BaseEvent> Context.subscribe(consumer: (EventType) -> Unit) = (this as? BaseActivity<*>)?.safeSubscribe(consumer)

fun emptyString(): String {
    return EMPTY_STRING
}

val Any.TAG: String
    get() = javaClass.simpleName

fun Random.nextInt(from: Int, to: Int): Int {
    if (from == to) {
        return from
    }
    return nextInt(to - from) + from
}

fun <T> Random.randomItem(list: List<T>): T {
    return list[nextInt(0, list.size - 1)]
}