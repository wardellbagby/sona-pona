package com.wardellbagby.tokipona.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.wardellbagby.tokipona.ui.fragment.BaseFragment

/**
 * @author Wardell Bagby
 */

fun FragmentManager.getLastBackStackEntry(): FragmentManager.BackStackEntry? {
    return if (backStackEntryCount == 0) null else getBackStackEntryAt(backStackEntryCount - 1)
}

@Suppress("UNCHECKED_CAST") // We KNOW it's of type T 'cause we do a class check.
fun <T : Fragment> FragmentManager.findFragmentByClass(clazz: Class<T>): T? {
    return fragments.firstOrNull { it.javaClass == clazz } as? T
}

fun FragmentManager.isTagInBackstack(tag: String): Boolean {
    return (0..backStackEntryCount - 1).any {
        getBackStackEntryAt(it).name == tag
    }
}

fun FragmentManager.sendOnBackPressed(): Boolean {
    return fragments
            .filter { it.isAdded && it.isVisible && !it.isRemoving && it.isResumed }
            .any { it is BaseFragment && it.onBackPressed() }
}

val Any.TAG: String
    get() = javaClass.simpleName
