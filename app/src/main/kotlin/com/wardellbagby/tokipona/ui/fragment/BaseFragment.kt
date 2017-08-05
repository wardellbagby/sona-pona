package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.util.Fragments
import com.wardellbagby.tokipona.util.sendOnBackPressed

/**
 * @author Wardell Bagby
 */
open class BaseFragment : Fragment(), Pikkel by PikkelDelegate() {

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getTitle())
    }

    open fun onBackPressed(): Boolean {
        return childFragmentManager.sendOnBackPressed()
    }

    /**
     * Returns the title for this fragment. This will be set on the attached
     * [com.wardellbagby.tokipona.ui.activity.BaseActivity] in [onResume].
     */
    open fun getTitle(): CharSequence? {
        return getString(R.string.app_name)
    }

    open fun setTitle(title: CharSequence?) {
        activity?.title = title
    }

    /**
     * Returns a list of supported transition names to be used for animating transitions between
     * fragments.
     */
    open fun getSupportedTransitionNames(): List<String> {
        return listOf()
    }

    fun getSharedElementForTransition(transitionName: String): View? {
        return Fragments.getSharedElementForTransition(view, childFragmentManager, transitionName)
    }

    fun replace(@IdRes id: Int, fragment: Fragment, tag: String) {
        Fragments.replace(childFragmentManager, id, fragment, tag)
    }
}