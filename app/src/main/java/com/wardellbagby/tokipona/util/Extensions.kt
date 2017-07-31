package com.wardellbagby.tokipona.util

import android.support.v4.app.FragmentManager

/**
 * @author Wardell Bagby
 */

fun FragmentManager.getLastBackStackEntry(): FragmentManager.BackStackEntry? {
    return if (backStackEntryCount == 0) null else getBackStackEntryAt(backStackEntryCount - 1)
}

fun FragmentManager.isLastBackEntry(name: String): Boolean {
    return getLastBackStackEntry()?.name == name
}