package com.wardellbagby.tokipona.util

import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.IdRes
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.transition.AutoTransition
import android.transition.Fade
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.ui.fragment.BaseFragment

/**
 * Utility class containing useful methods for Fragments.
 * @author Wardell Bagby
 */
object Fragments {
    fun replace(manager: FragmentManager, @IdRes id: Int, fragmentToAdd: Fragment, tag: String) {
        val transaction = manager.beginTransaction().addToBackStack(tag)
        val currentFragment: Fragment? = manager.findFragmentById(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && currentFragment is BaseFragment) {
            addTransitionsToTransaction(currentFragment, fragmentToAdd, transaction)
        }

        transaction
                .setReorderingAllowed(true)
                .replace(id, fragmentToAdd, tag)
                .commit()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionsToTransaction(currentFragment: Fragment?, fragmentToAdd: Fragment, transaction: FragmentTransaction) {
        if (currentFragment is BaseFragment) {
            currentFragment.getSupportedTransitionNames().map(currentFragment::getSharedElementForTransition)
                    .filter { it != null }
                    .forEach { transaction.addSharedElement(it, it?.transitionName) }
            fragmentToAdd.apply {
                val sharedElementTransitions = AutoTransition()
                val defaultTransition = Fade()

                currentFragment.getTargetsToExcludeFromTransitions().forEach {
                    sharedElementTransitions.excludeChildren(it, true)
                    defaultTransition.excludeChildren(it, true)
                }

                allowEnterTransitionOverlap = false
                allowReturnTransitionOverlap = false
                sharedElementEnterTransition = sharedElementEnterTransition
                sharedElementReturnTransition = sharedElementReturnTransition
                reenterTransition = defaultTransition
                enterTransition = defaultTransition
                returnTransition = defaultTransition
                exitTransition = defaultTransition
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getSharedElementForTransition(rootView: View?, manager: FragmentManager, transitionName: String): View? {
        return getSharedElementFromView(rootView, transitionName) ?: getSharedElementFromManager(manager, transitionName)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getSharedElementFromView(view: View?, transitionName: String): View? {
        if (view !is ViewGroup) {
            return null
        }
        if (view.transitionName == transitionName) {
            return view
        }
        val element = (0..view.childCount).map(view::getChildAt)
                .firstOrNull {
                    it != null && it.transitionName == transitionName
                }
        return element
    }

    private fun getSharedElementFromManager(manager: FragmentManager, transitionName: String): View? {
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