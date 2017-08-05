package com.wardellbagby.tokipona.util

import android.os.Build
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.transition.AutoTransition
import android.transition.Slide
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.ui.fragment.BaseFragment

/**
 * @author Wardell Bagby
 */
object Fragments {
    fun replace(manager: FragmentManager, @IdRes id: Int, fragmentToAdd: Fragment, tag: String) {
        val transaction = manager.beginTransaction().addToBackStack(tag)
        val currentFragment = manager.findFragmentById(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && currentFragment is BaseFragment) {
            currentFragment.getSupportedTransitionNames().map(currentFragment::getSharedElementForTransition)
                    .filter { it != null }
                    .forEach { transaction.addSharedElement(it, it?.transitionName) }
            fragmentToAdd.apply {
                sharedElementEnterTransition = AutoTransition()
                sharedElementReturnTransition = AutoTransition()
                enterTransition = Slide()
                exitTransition = Slide()
            }
        }

        transaction
                .setReorderingAllowed(true)
                .replace(id, fragmentToAdd, tag)
                .commit()
    }

    fun getSharedElementForTransition(rootView: View?, manager: FragmentManager, transitionName: String): View? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null
        }

        if (rootView is ViewGroup) {
            val element = (0..rootView.childCount).map(rootView::getChildAt)
                    .firstOrNull {
                        it != null && it.transitionName == transitionName
                    }
            if (element != null) {
                return element
            }
        }
        if (manager.fragments.isNotEmpty()) {
            manager.fragments.filter {
                it is BaseFragment
            }.map {
                it as BaseFragment
            }.forEach {
                val element = it.getSharedElementForTransition(transitionName)
                if (element != null) {
                    return element
                }
            }
        }
        return null
    }
}