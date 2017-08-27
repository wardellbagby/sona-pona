package com.wardellbagby.tokipona.util

import android.os.Build
import android.support.annotation.IdRes
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.transition.AutoTransition
import android.transition.Transition
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setFragmentTransitions(currentFragment, fragmentToAdd)
        }

        transaction
                .setReorderingAllowed(true)
                .replace(id, fragmentToAdd, tag)
                .commit()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setFragmentTransitions(currentFragment: Fragment?, fragmentToAdd: Fragment) {
        applyTransitionsToFragment(currentFragment)
        applyTransitionsToFragment(fragmentToAdd)
        if (currentFragment is BaseFragment) {
            excludeChildrenFromTransitions(currentFragment, currentFragment.getTargetsToExcludeFromTransitions())
            excludeChildrenFromTransitions(fragmentToAdd, currentFragment.getTargetsToExcludeFromTransitions())
        }
    }

    private fun excludeChildrenFromTransitions(fragment: Fragment, children: List<View>) {
        children.forEach {
            fragment.apply {
                excludeChildFromTransition(enterTransition, it)
                excludeChildFromTransition(exitTransition, it)
                excludeChildFromTransition(reenterTransition, it)
                excludeChildFromTransition(returnTransition, it)
            }
        }
    }

    private fun excludeChildFromTransition(transition: Any, child: View) = (transition as? Transition)?.excludeTarget(child, true)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun applyTransitionsToFragment(fragment: Fragment?) {
        fragment?.apply {
            allowEnterTransitionOverlap = false
            allowReturnTransitionOverlap = false

            reenterTransition = getDefaultEnterTransition()
            enterTransition = getDefaultEnterTransition()
            returnTransition = getDefaultExitTransition()
            exitTransition = getDefaultExitTransition()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP) private fun getDefaultEnterTransition() = AutoTransition()
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP) private fun getDefaultExitTransition() = AutoTransition()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
        return (0..view.childCount).map(view::getChildAt)
                .firstOrNull {
                    it != null && it.transitionName == transitionName
                }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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