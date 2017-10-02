package com.wardellbagby.tokipona.util

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.util.SortedList
import com.wardellbagby.tokipona.ui.activity.BaseActivity
import com.wardellbagby.tokipona.ui.fragment.BaseFragment
import java.util.Random

/**
 * @author Wardell Bagby
 */

private const val EMPTY_STRING = ""

/**
 * Returns the last entry currently on the backstack, or null if there isn't any.
 */
fun FragmentManager.getLastBackStackEntry(): FragmentManager.BackStackEntry? {
    return if (backStackEntryCount == 0) null else getBackStackEntryAt(backStackEntryCount - 1)
}

/**
 * Calls [BaseFragment.onBackPressed] for any currently added, visible, and resumed fragments, in a
 * non-determinate ordering. Returns true if any fragment handled it, false otherwise.
 */
fun FragmentManager.sendOnBackPressed(): Boolean {
    return fragments
            .filter { it.isAdded && it.isVisible && !it.isRemoving && it.isResumed }
            .any { it is BaseFragment && it.onBackPressed() }
}

/**
 * Convenience method for calling [BaseActivity.safeSubscribe]
 */
fun <EventType : BaseActivity.BaseEvent> Context.subscribe(consumer: (EventType) -> Unit) = (this as? BaseActivity<*>)?.safeSubscribe(consumer)

/**
 * Returns an empty string.
 */
fun emptyString(): String {
    return EMPTY_STRING
}

/**
 * A tag useful for logging.
 */
val Any.TAG: String
    get() = javaClass.simpleName

/**
 * Returns a random integer in range [from] inclusive - [to] exclusive.
 */
fun Random.nextInt(from: Int, to: Int): Int {
    if (from == to) {
        return from
    }
    return nextInt(to - from) + from
}

/**
 * Returns a random item from the provided list.
 */
fun <T> Random.randomItem(list: List<T>): T {
    return list[nextInt(0, list.size - 1)]
}

/**
 * Allows += to be used on [SortedList]
 */
operator fun <T> SortedList<T>.plusAssign(values: Collection<T>) {
    addAll(values)
}